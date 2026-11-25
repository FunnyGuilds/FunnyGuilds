package net.dzikoysk.funnyguilds.data

import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.Instant
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.SECONDS
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.TestLogger
import net.dzikoysk.funnyguilds.data.database.Database
import net.dzikoysk.funnyguilds.data.database.SQLDataModel
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseGuildSerializer
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseRegionSerializer
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.Region
import net.dzikoysk.funnyguilds.user.FakeUserProfile
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.file.YamlConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.startsWith
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PersistenceOrderingTest : FunnyGuildsSpec() {

    @Test
    fun `failed SQL write is retried and does not stop other records`() {
        config.regionsEnabled = false
        val connection = mock(Connection::class.java)
        val statement = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(anyString())).thenReturn(statement)
        `when`(statement.executeUpdate()).thenThrow(SQLException("write failed")).thenReturn(1)
        val model = sqlModel(connection)
        val users = listOf(newUser("Steve"), newUser("Alex"))

        model.save(true)
        assertEquals(1, users.count { it.wasChanged() })

        model.save(true)
        assertTrue(users.none { it.wasChanged() })
        verify(statement, times(3)).executeUpdate()
    }

    @Test
    fun `unavailable database leaves the record pending`() {
        config.regionsEnabled = false
        val model = SQLDataModel(funnyGuilds)
        `when`(funnyGuilds.dataModel).thenReturn(model)
        `when`(funnyGuilds.database).thenReturn(Optional.empty())
        val user = newUser("Steve")

        model.save(true)

        assertTrue(user.wasChanged())
    }

    @Test
    fun `failed points update schedules a retry of the guild record`() {
        config.regionsEnabled = false
        val connection = mock(Connection::class.java)
        val pointsUpdate = mock(PreparedStatement::class.java)
        val upsert = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(startsWith("UPDATE"))).thenReturn(pointsUpdate)
        `when`(connection.prepareStatement(startsWith("INSERT"))).thenReturn(upsert)
        `when`(pointsUpdate.executeUpdate()).thenThrow(SQLException("write failed"))
        val model = sqlModel(connection)
        val guild = newGuild("Guild", "TAG")
        guild.owner.markUnchanged()
        guild.markUnchanged()

        DatabaseGuildSerializer.updatePoints(guild)
        assertTrue(guild.wasChanged())

        model.save(true)
        assertFalse(guild.wasChanged())
        verify(upsert).executeUpdate()
    }

    @Test
    fun `a user mutation during JDBC IO remains pending for the next save`() {
        config.regionsEnabled = false
        val connection = mock(Connection::class.java)
        val statement = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(anyString())).thenReturn(statement)
        val model = sqlModel(connection)
        val user = newUser("Steve")
        val oldPoints = user.rank.points

        Executors.newSingleThreadExecutor().use { worker ->
            `when`(statement.executeUpdate()).thenAnswer {
                worker.submit { user.rank.points = oldPoints + 7 }.get(5, SECONDS)
                1
            }.thenReturn(1)

            model.save(true)
            assertTrue(user.wasChanged())
            model.save(true)
            assertFalse(user.wasChanged())
        }
        verify(statement, times(2)).executeUpdate()
    }

    @Test
    fun `deletion follows an in-flight autosave and stale writes stay blocked`() {
        config.regionsEnabled = false
        val connection = mock(Connection::class.java)
        val upsert = mock(PreparedStatement::class.java)
        val delete = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(startsWith("INSERT"))).thenReturn(upsert)
        `when`(connection.prepareStatement(startsWith("DELETE"))).thenReturn(delete)
        val model = sqlModel(connection)
        val guild = newGuild("Guild", "TAG")
        val deleting = CountDownLatch(1)
        val upsertStarted = CountDownLatch(1)

        Executors.newSingleThreadExecutor().use { worker ->
            val deletion = worker.submit {
                assertTrue(upsertStarted.await(5, SECONDS))
                mockStatic(FunnyGuilds::class.java).use { statics ->
                    statics.`when`<FunnyGuilds> { FunnyGuilds.getInstance() }.thenReturn(funnyGuilds)
                    deleting.countDown()
                    guildManager.deleteGuild(funnyGuilds, guild)
                    model.flushDeletes()
                }
            }
            `when`(upsert.executeUpdate()).thenAnswer {
                upsertStarted.countDown()
                assertTrue(deleting.await(5, SECONDS))
                1
            }

            // The owner is already persisted for this scenario. Only the guild is written.
            guild.owner.markUnchanged()
            model.save(true)
            deletion.get(5, SECONDS)
        }

        DatabaseGuildSerializer.serialize(guild)
        assertTrue(guildManager.findByUuid(guild.uuid).isEmpty)
        val order = inOrder(upsert, delete)
        order.verify(upsert).executeUpdate()
        order.verify(delete).executeUpdate()
        verify(upsert, times(1)).executeUpdate()
    }

    @Test
    fun `shutdown writes a newer snapshot after autosave and before closing the database`() {
        config.regionsEnabled = false
        val connection = mock(Connection::class.java)
        val statement = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(anyString())).thenReturn(statement)
        val model = sqlModel(connection)
        val database = funnyGuilds.database.orElseThrow()
        val handler = DataPersistenceHandler(funnyGuilds)
        val user = newUser("Steve")
        val originalPoints = user.rank.points
        val finalPoints = originalPoints + 7
        val pointsParameter = model.usersTable.mapElementsKey.getValue("points")
        val autosaveWriting = CountDownLatch(1)
        val shutdownStarting = CountDownLatch(1)

        `when`(statement.executeUpdate()).thenAnswer {
            user.rank.points = finalPoints
            autosaveWriting.countDown()
            assertTrue(shutdownStarting.await(5, SECONDS))
            1
        }.thenReturn(1)

        Executors.newSingleThreadExecutor().use { worker ->
            val shutdown = worker.submit {
                assertTrue(autosaveWriting.await(5, SECONDS))
                mockStatic(FunnyGuilds::class.java).use { statics ->
                    statics.`when`<FunnyGuilds> { FunnyGuilds.getInstance() }.thenReturn(funnyGuilds)
                    shutdownStarting.countDown()
                    handler.shutdown()
                }
            }
            model.save(true)
            shutdown.get(5, SECONDS)
        }

        user.rank.points = finalPoints + 1
        model.save(false)

        val order = inOrder(statement, database)
        order.verify(statement).setObject(pointsParameter, originalPoints)
        order.verify(statement).executeUpdate()
        order.verify(statement).setObject(pointsParameter, finalPoints)
        order.verify(statement).executeUpdate()
        order.verify(statement).close()
        order.verify(database).shutdown()
        verify(statement, times(2)).executeUpdate()
        assertTrue(user.wasChanged())
    }

    @Test
    fun `mutation during flat file IO is written on the next autosave`(@TempDir directory: Path) {
        config.regionsEnabled = false
        `when`(funnyGuilds.pluginDataFolder).thenReturn(directory.toFile())
        val model = FlatDataModel(funnyGuilds)
        `when`(funnyGuilds.dataModel).thenReturn(model)
        val user = newUser("Steve")
        val file = directory.resolve("users").resolve("${user.uuid}.yml")
        val originalPoints = user.rank.points
        Files.createDirectories(file.parent)

        Executors.newSingleThreadExecutor().use { worker ->
            assertTrue(model.persist(user, { user.rank.points }) { points ->
                worker.submit { user.rank.points = originalPoints + 7 }.get(5, SECONDS)
                Files.writeString(file, "points: $points\n")
                true
            })
        }
        assertEquals(originalPoints, YamlConfiguration.loadConfiguration(file.toFile()).getInt("points"))
        assertTrue(user.wasChanged())

        model.save(true)
        assertEquals(originalPoints + 7, YamlConfiguration.loadConfiguration(file.toFile()).getInt("points"))
        assertFalse(user.wasChanged())
    }

    @Test
    fun `flat file failure leaves changes pending and final save closes writes`(@TempDir directory: Path) {
        config.regionsEnabled = false
        `when`(funnyGuilds.pluginDataFolder).thenReturn(directory.toFile())
        val model = FlatDataModel(funnyGuilds)
        `when`(funnyGuilds.dataModel).thenReturn(model)
        val user = newUser("Steve")
        val file = directory.resolve("users").resolve("${user.uuid}.yml")
        Files.createDirectories(file)

        model.save(true)
        assertTrue(user.wasChanged())
        Files.delete(file)

        model.saveAndClose()
        assertFalse(user.wasChanged())
        val points = user.rank.points
        user.rank.points = points + 1
        model.save(true)

        assertEquals(points, YamlConfiguration.loadConfiguration(file.toFile()).getInt("points"))
        assertTrue(user.wasChanged())
    }

    @Test
    fun `failed flat deletion is retried before reusing a guild name`(@TempDir directory: Path) {
        config.regionsEnabled = false
        `when`(funnyGuilds.pluginDataFolder).thenReturn(directory.toFile())
        val model = FlatDataModel(funnyGuilds)
        `when`(funnyGuilds.dataModel).thenReturn(model)
        val oldGuild = newGuild("Guild", "OLD")
        val file = directory.resolve("guilds/Guild.yml")
        Files.createDirectories(file)
        val blocker = Files.createFile(file.resolve("blocker"))

        guildManager.removeGuild(oldGuild)
        model.deleteGuild(oldGuild)
        val replacement = newGuild("Guild", "NEW")
        model.save(true)
        assertTrue(replacement.wasChanged())

        Files.delete(blocker)
        model.save(true)
        assertFalse(replacement.wasChanged())
        assertEquals("NEW", YamlConfiguration.loadConfiguration(file.toFile()).getString("tag"))

        model.deleteGuild(oldGuild)
        assertEquals("NEW", YamlConfiguration.loadConfiguration(file.toFile()).getString("tag"))
    }

    @Test
    fun `failed flat rename leaves the old file readable until the replacement is saved`(@TempDir directory: Path) {
        config.regionsEnabled = false
        `when`(funnyGuilds.pluginDataFolder).thenReturn(directory.toFile())
        val model = FlatDataModel(funnyGuilds)
        `when`(funnyGuilds.dataModel).thenReturn(model)
        val guild = newGuild("GuildA", "TAG")
        model.save(true)
        val oldFile = directory.resolve("guilds/GuildA.yml")
        val newFile = directory.resolve("guilds/GuildB.yml")
        Files.createDirectory(newFile)

        model.renameGuild(guild, "GuildB")
        model.save(true)
        assertEquals("GuildA", YamlConfiguration.loadConfiguration(oldFile.toFile()).getString("name"))

        Files.delete(newFile)
        model.save(true)
        assertEquals("GuildB", YamlConfiguration.loadConfiguration(newFile.toFile()).getString("name"))
        assertFalse(Files.exists(oldFile))
    }

    @Test
    fun `deleting an unpersisted guild with a reserved name preserves the old guild file`(@TempDir directory: Path) {
        config.regionsEnabled = false
        `when`(funnyGuilds.pluginDataFolder).thenReturn(directory.toFile())
        val model = FlatDataModel(funnyGuilds)
        `when`(funnyGuilds.dataModel).thenReturn(model)
        val original = newGuild("OldName", "OLD")
        model.save(true)
        val originalFile = directory.resolve("guilds/OldName.yml")
        val replacementFile = directory.resolve("guilds/NewName.yml")
        assertTrue(model.tryRenameGuild(original, "NewName"))
        assertTrue(model.isGuildNameReserved("oldname"))

        // A caller may bypass command validation and add a guild directly.
        val conflicting = newGuild("OldName", "NEW")
        guildManager.removeGuild(conflicting)
        model.deleteGuild(conflicting)
        assertEquals(original.uuid.toString(), YamlConfiguration.loadConfiguration(originalFile.toFile()).getString("uuid"))

        model.save(true)
        assertFalse(Files.exists(originalFile))
        assertEquals(original.uuid.toString(), YamlConfiguration.loadConfiguration(replacementFile.toFile()).getString("uuid"))
        assertFalse(model.isGuildNameReserved("OldName"))
    }

    @Test
    fun `competing renames cannot use each others reserved names and both make progress`(@TempDir directory: Path) {
        config.regionsEnabled = false
        `when`(funnyGuilds.pluginDataFolder).thenReturn(directory.toFile())
        val model = FlatDataModel(funnyGuilds)
        `when`(funnyGuilds.dataModel).thenReturn(model)
        val first = newGuild("First", "ONE")
        val second = newGuild("Second", "TWO")
        model.save(true)

        assertTrue(model.tryRenameGuild(first, "FirstNew"))
        assertTrue(model.tryRenameGuild(second, "SecondNew"))
        assertFalse(model.tryRenameGuild(first, "Second"))
        assertFalse(model.tryRenameGuild(second, "first"))
        assertEquals("FirstNew", first.name)
        assertEquals("SecondNew", second.name)

        model.save(true)
        assertFalse(model.isGuildNameReserved("First"))
        assertFalse(model.isGuildNameReserved("Second"))
        assertTrue(model.tryRenameGuild(second, "First"))
        model.save(true)
        val firstFile = directory.resolve("guilds/First.yml").toFile()
        assertEquals(second.uuid.toString(), YamlConfiguration.loadConfiguration(firstFile).getString("uuid"))
    }

    @Test
    fun `shutdown reports a deletion that still fails`() {
        val connection = mock(Connection::class.java)
        val statement = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(startsWith("DELETE"))).thenReturn(statement)
        `when`(statement.executeUpdate()).thenThrow(SQLException("delete failed"))
        val model = sqlModel(connection)
        val guild = Guild("Guild", "TAG")
        model.queueGuildDeletion(guild)

        DataPersistenceHandler(funnyGuilds).shutdown()

        val errors = (FunnyGuilds.getPluginLogger() as TestLogger).errors
        assertTrue(errors.any { it.contains("Uncompleted storage deletions at shutdown") && it.contains(guild.uuid.toString()) })
        verify(funnyGuilds.database.orElseThrow()).shutdown()
    }

    @Test
    fun `failed SQL writes keep both guilds and regions pending`() {
        config.regionsEnabled = true
        val connection = mock(Connection::class.java)
        val guildWrite = mock(PreparedStatement::class.java)
        val regionWrite = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(startsWith("INSERT INTO `${config.mysql.guildsTableName}`"))).thenReturn(guildWrite)
        `when`(connection.prepareStatement(startsWith("INSERT INTO `${config.mysql.regionsTableName}`"))).thenReturn(regionWrite)
        for (statement in listOf(guildWrite, regionWrite)) {
            `when`(statement.executeUpdate()).thenThrow(SQLException("write failed")).thenReturn(1)
        }
        val model = sqlModel(connection)
        val guild = newGuild("Guild", "TAG")
        guild.owner.markUnchanged()
        val region = addRegion(guild)

        model.save(true)
        assertTrue(guild.wasChanged())
        assertTrue(region.wasChanged())
        model.save(true)
        assertFalse(guild.wasChanged())
        assertFalse(region.wasChanged())
    }

    @Test
    fun `SQL rename retains the previous region until both replacement records are written`() {
        config.regionsEnabled = true
        val connection = mock(Connection::class.java)
        val guildWrite = mock(PreparedStatement::class.java)
        val regionWrite = mock(PreparedStatement::class.java)
        val delete = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(startsWith("INSERT INTO `${config.mysql.guildsTableName}`"))).thenReturn(guildWrite)
        `when`(connection.prepareStatement(startsWith("INSERT INTO `${config.mysql.regionsTableName}`"))).thenReturn(regionWrite)
        `when`(connection.prepareStatement(startsWith("DELETE"))).thenReturn(delete)
        `when`(guildWrite.executeUpdate()).thenThrow(SQLException("rename failed")).thenReturn(1)
        val model = sqlModel(connection)
        val guild = newGuild("GuildA", "TAG")
        guild.owner.markUnchanged()
        val region = addRegion(guild)

        model.renameGuild(guild, "GuildB")
        assertTrue(model.isGuildNameReserved("guilda"))
        model.deleteRegion(Region("GuildA", region.center))
        model.save(true)
        verify(delete, times(0)).executeUpdate()

        model.save(true)
        val order = inOrder(guildWrite, regionWrite, delete)
        order.verify(regionWrite).executeUpdate()
        order.verify(guildWrite).executeUpdate()
        order.verify(regionWrite).executeUpdate()
        order.verify(guildWrite).executeUpdate()
        order.verify(delete).executeUpdate()
        assertFalse(model.isGuildNameReserved("GuildA"))
    }

    @Test
    fun `region deletion is queued while autosave is still writing`() {
        config.regionsEnabled = true
        val connection = mock(Connection::class.java)
        val upsert = mock(PreparedStatement::class.java)
        val delete = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(startsWith("INSERT"))).thenReturn(upsert)
        `when`(connection.prepareStatement(startsWith("DELETE"))).thenReturn(delete)
        val model = sqlModel(connection)
        val manager = funnyGuilds.regionManager
        val region = addRegion(Guild("Guild", "TAG"))

        Executors.newSingleThreadExecutor().use { worker ->
            `when`(upsert.executeUpdate()).thenAnswer {
                worker.submit { manager.deleteRegion(model, region) }.get(5, SECONDS)
                1
            }
            model.save(true)
        }
        assertTrue(manager.getRegions().isEmpty())
        model.flushDeletes()
        DatabaseRegionSerializer.serialize(region)

        val order = inOrder(upsert, delete)
        order.verify(upsert).executeUpdate()
        order.verify(delete).executeUpdate()
        verify(upsert, times(1)).executeUpdate()
    }

    @Test
    fun `flat loaders ignore unfinished temporary guild and region files`(@TempDir directory: Path) {
        config.regionsEnabled = true
        `when`(funnyGuilds.pluginDataFolder).thenReturn(directory.toFile())
        `when`(funnyGuilds.dataFolder).thenReturn(directory.resolve("legacy").toFile())
        val model = FlatDataModel(funnyGuilds)
        for (folder in listOf("guilds", "regions")) {
            Files.createDirectories(directory.resolve(folder))
            Files.writeString(directory.resolve("$folder/.funnyguilds-interrupted.tmp"), "name: Unfinished\n")
        }

        model.load()

        assertTrue(guildManager.getGuilds().isEmpty())
        assertTrue(funnyGuilds.regionManager.getRegions().isEmpty())
        assertTrue((FunnyGuilds.getPluginLogger() as TestLogger).errors.isEmpty())
    }

    private fun addRegion(guild: Guild): Region {
        val world = mock(World::class.java)
        `when`(world.name).thenReturn("world")
        `when`(world.maxHeight).thenReturn(256)
        val region = Region(guild, Location(world, 0.0, 64.0, 0.0), 4)
        guild.setRegion(region)
        funnyGuilds.regionManager.addRegion(region)
        return region
    }

    private fun sqlModel(connection: Connection): SQLDataModel {
        val database = mock(Database::class.java)
        `when`(funnyGuilds.database).thenReturn(Optional.of(database))
        `when`(database.connection).thenReturn(connection)
        val model = SQLDataModel(funnyGuilds)
        `when`(funnyGuilds.dataModel).thenReturn(model)
        return model
    }

    private fun newUser(name: String) =
        userManager.createFake(UUID.randomUUID(), name, FakeUserProfile(false, false, 0))

    private fun newGuild(name: String, tag: String): Guild {
        val guild = Guild(name, tag)
        guild.owner = userManager.findByName("Steve").orElseGet { newUser("Steve") }
        guild.validity = Instant.now().plusSeconds(60)
        guild.protection = Instant.now()
        return guildManager.addGuild(guild)
    }
}
