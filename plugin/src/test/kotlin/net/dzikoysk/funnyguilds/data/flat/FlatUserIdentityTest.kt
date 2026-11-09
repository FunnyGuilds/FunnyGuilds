package net.dzikoysk.funnyguilds.data.flat

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
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

class FlatUserIdentityTest : FunnyGuildsSpec() {

    @TempDir
    lateinit var folder: Path

    @BeforeEach
    fun prepareFolders() {
        lenient().`when`(funnyGuilds.dataFolder).thenReturn(folder.toFile())
        lenient().`when`(funnyGuilds.pluginDataFolder).thenReturn(folder.resolve("data").toFile())
    }

    @Test
    fun `conflict aborts before clearing users loading guilds or moving legacy files`() {
        val existing = userManager.createFake(UUID.randomUUID(), "Existing")
        val first = writeUser("data/users", "odd-legacy-filename.yml", "Steve")
        val second = writeUser("users", "another-legacy-filename.yml", "steve")
        val before = snapshot()

        val error = assertFailsWith<IllegalStateException> { FlatDataModel(funnyGuilds).load() }

        assertContains(error.message!!, first.toString())
        assertContains(error.message!!, second.toString())
        assertEquals(before, snapshot())
        assertEquals(1, userManager.countUsers())
        assertSame(existing, userManager.findByName("Existing").get())
        verify(funnyGuilds, never()).guildManager
        verify(funnyGuilds, never()).regionManager
    }

    @Test
    fun `conflicting modern files are detected before any user is created`() {
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
        val file = folder.resolve("data/users/${UUID.randomUUID()}.yml")
        Files.createDirectories(file.parent)
        for (content in listOf("name: [unclosed", "uuid: not-a-uuid\nname: Steve\n",
                "uuid: ${UUID.randomUUID()}\n")) {
            Files.writeString(file, content)
            val before = snapshot()
            val error = assertFailsWith<IllegalStateException> { FlatDataModel(funnyGuilds).load() }
            assertContains(error.message!!, file.toString())
            assertEquals(before, snapshot())
            assertEquals(0, userManager.countUsers())
        }
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
