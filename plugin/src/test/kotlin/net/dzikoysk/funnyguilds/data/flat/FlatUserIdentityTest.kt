package net.dzikoysk.funnyguilds.data.flat

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.TestLogger
import net.dzikoysk.funnyguilds.data.DataModelLoader
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.api.io.CleanupMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import panda.std.Option
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.assertNull

class FlatUserIdentityTest : FunnyGuildsSpec() {

    @TempDir(cleanup = CleanupMode.ALWAYS)
    lateinit var folder: Path

    @BeforeEach
    fun prepareFolders() {
        lenient().`when`(funnyGuilds.dataFolder).thenReturn(folder.toFile())
        lenient().`when`(funnyGuilds.pluginDataFolder).thenReturn(folder.resolve("data").toFile())
    }

    @Test
    fun `conflict aborts before clearing users loading guilds or moving legacy files`() {
        config.preventUuidConflicts = true
        val existing = userManager.createFake(UUID.randomUUID(), "Existing")
        val first = writeUser("data/users", "odd-legacy-filename.yml", "Steve")
        val second = writeUser("users", "another-legacy-filename.yml", "Steve")
        val before = snapshot()

        val result = DataModelLoader(FunnyGuilds.getPluginLogger()).load { FlatDataModel(funnyGuilds) }

        assertTrue(result.isErr())
        assertContains(result.getError(), "User data validation failed")
        val message = (FunnyGuilds.getPluginLogger() as TestLogger).errors.single()
        assertContains(message, first.toString())
        assertContains(message, second.toString())
        assertEquals(before, snapshot())
        assertEquals(1, userManager.countUsers())
        assertSame(existing, userManager.findByName("Existing").get())
        verify(funnyGuilds, never()).guildManager
        verify(funnyGuilds, never()).regionManager
    }

    @Test
    fun `conflicting modern files are detected before any user is created`() {
        config.preventUuidConflicts = true
        writeUser("data/users", "${UUID.randomUUID()}.yml", "Steve")
        writeUser("data/users", "${UUID.randomUUID()}.yml", "Steve")
        val before = snapshot()

        assertFailsWith<IllegalStateException> { FlatDataModel(funnyGuilds).load() }

        assertEquals(0, userManager.countUsers())
        assertEquals(before, snapshot())
        verify(funnyGuilds, never()).guildManager
    }

    @Test
    fun `valid users load their original stats from modern and legacy folders`() {
        prepareProfiles()
        for (source in listOf("data/users", "users")) {
            val uuid = UUID.randomUUID()
            val file = writeUser(source, "$uuid.yml", "Steve", uuid)
            FlatDataModel(funnyGuilds).load()
            val user = userManager.findByUuid(uuid).get()
            assertEquals("Steve", user.name)
            assertEquals(1234, user.rank.points)
            assertTrue(!user.wasChanged())
            Files.delete(folder.resolve("data/users/$uuid.yml"))
            Files.delete(folder.resolve("data/users"))
            assertTrue(!Files.exists(file))
        }
    }

    @Test
    fun `malformed YAML and invalid identities fail without changing files`() {
        config.preventUuidConflicts = true
        val file = folder.resolve("data/users/${UUID.randomUUID()}.yml")
        Files.createDirectories(file.parent)
        for (content in listOf("name: [unclosed", "uuid: not-a-uuid\nname: Steve\n",
                "uuid: ${UUID.randomUUID()}\n")) {
            Files.writeString(file, content)
            val before = snapshot()
            val logger = FunnyGuilds.getPluginLogger() as TestLogger
            logger.errors.clear()
            val error = assertFailsWith<IllegalStateException> { FlatDataModel(funnyGuilds).load() }
            assertNull(error.cause)
            assertContains(logger.errors.single(), file.toString())
            assertEquals(before, snapshot())
            assertEquals(0, userManager.countUsers())
        }
    }

    @Test
    fun `many unreadable YAML files produce one summary without stacktraces or partial loading`() {
        config.preventUuidConflicts = true
        val users = Files.createDirectories(folder.resolve("data/users"))
        repeat(30) {
            Files.writeString(users.resolve("${UUID.randomUUID()}.yml"), "name: [unclosed")
        }
        writeUser("data/users", "${UUID.randomUUID()}.yml", "ValidPlayer")
        val before = snapshot()

        val error = assertFailsWith<IllegalStateException> { FlatDataModel(funnyGuilds).load() }

        assertNull(error.cause)
        val summary = (FunnyGuilds.getPluginLogger() as TestLogger).errors.single()
        assertContains(summary, "Could not read 30 user file(s)")
        assertContains(summary, users.toString())
        assertTrue(!summary.contains('\n'))
        assertEquals(0, userManager.countUsers())
        assertEquals(before, snapshot())
        verify(funnyGuilds, never()).guildManager
        verify(funnyGuilds, never()).regionManager
    }

    @Test
    fun `disabled validation permits exact duplicate names without merging UUID accounts`() {
        assertTrue(!config.preventUuidConflicts)
        prepareProfiles()
        val first = UUID.randomUUID()
        val second = UUID.randomUUID()
        writeUser("data/users", "$first.yml", "Steve", first)
        writeUser("data/users", "$second.yml", "Steve", second)
        val before = snapshot()
        FlatDataModel(funnyGuilds).load()
        assertEquals(2, userManager.countUsers())
        assertTrue(userManager.findByUuid(first).isPresent)
        assertTrue(userManager.findByUuid(second).isPresent)
        assertEquals(before, snapshot())
    }

    @Test
    fun `enabled validation permits offline mode case variants`() {
        config.preventUuidConflicts = true
        prepareProfiles()
        writeUser("data/users", "${UUID.randomUUID()}.yml", "Steve")
        writeUser("data/users", "${UUID.randomUUID()}.yml", "steve")
        FlatDataModel(funnyGuilds).load()
        assertEquals(2, userManager.countUsers())
        assertEquals("Steve", userManager.findByName("Steve").get().name)
        assertEquals("steve", userManager.findByName("steve").get().name)
    }

    @Test
    fun `disabled validation skips unreadable YAML with a single summary`() {
        prepareProfiles()
        val users = Files.createDirectories(folder.resolve("data/users"))
        Files.writeString(users.resolve("broken.yml"), "name: [unclosed")
        writeUser("data/users", "${UUID.randomUUID()}.yml", "Steve")
        FlatDataModel(funnyGuilds).load()
        assertEquals(1, userManager.countUsers())
        assertContains((FunnyGuilds.getPluginLogger() as TestLogger).errors.single(), "Could not read 1 user file(s)")
    }

    private fun prepareProfiles() {
        val server = mock(FunnyServer::class.java)
        lenient().`when`(server.getPlayer(any(UUID::class.java))).thenReturn(Option.none())
        lenient().`when`(funnyGuilds.funnyServer).thenReturn(server)
    }

    private fun writeUser(directory: String, filename: String, name: String, uuid: UUID = UUID.randomUUID()): Path {
        val file = folder.resolve(directory).resolve(filename)
        Files.createDirectories(file.parent)
        Files.writeString(file, "uuid: $uuid\nname: $name\npoints: 1234\n")
        return file
    }

    private fun snapshot(): Map<String, String> = Files.walk(folder).use { paths ->
        paths.filter(Files::isRegularFile).toList().associate {
            folder.relativize(it).toString() to Files.readString(it)
        }
    }
}
