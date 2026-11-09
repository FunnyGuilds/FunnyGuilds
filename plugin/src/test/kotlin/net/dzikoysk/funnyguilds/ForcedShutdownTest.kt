package net.dzikoysk.funnyguilds

import net.dzikoysk.funnyguilds.data.DataModel
import net.dzikoysk.funnyguilds.config.message.MessageService
import org.bukkit.Server
import org.bukkit.plugin.PluginManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.ArgumentMatchers.contains
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doCallRealMethod
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import panda.std.Option
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ForcedShutdownTest : FunnyGuildsSpec() {

    @TempDir
    lateinit var folder: Path

    @Test
    fun `onEnable aborts on real YAML conflict before starting persistence handlers`() {
        val first = UUID.randomUUID()
        val second = UUID.randomUUID()
        val users = Files.createDirectories(folder.resolve("data/users"))
        Files.writeString(users.resolve("$first.yml"), "uuid: $first\nname: Steve\n")
        Files.writeString(users.resolve("$second.yml"), "uuid: $second\nname: steve\n")
        `when`(funnyGuilds.pluginDataFolder).thenReturn(folder.resolve("data").toFile())
        config.scoreboard.enabled = false
        field("pluginConfiguration").set(funnyGuilds, config)
        val languageFolder = folder.resolve("lang").toFile()
        field("pluginLanguageFolderFile").set(funnyGuilds, languageFolder)
        for (name in listOf("nameTagUpdateTask", "dummyUpdateTask", "scoreboardQueueUpdateTask")) {
            field(name).set(funnyGuilds, Option.none<Any>())
        }
        val server = mock(Server::class.java)
        val pluginManager = mock(PluginManager::class.java)
        `when`(funnyGuilds.server).thenReturn(server)
        `when`(server.pluginManager).thenReturn(pluginManager)
        doCallRealMethod().`when`(funnyGuilds).onEnable()
        doCallRealMethod().`when`(funnyGuilds).onDisable()
        doCallRealMethod().`when`(funnyGuilds).shutdown(org.mockito.ArgumentMatchers.anyString())
        doAnswer { funnyGuilds.onDisable() }.`when`(pluginManager).disablePlugin(funnyGuilds)

        val logger = mock(FunnyGuildsLogger::class.java)
        val previousLogger = field("logger").get(null)
        field("logger").set(null, logger)
        try {
            mockStatic(MessageService::class.java).use { messages ->
                messages.`when`<MessageService> { MessageService.prepareMessageService(funnyGuilds, languageFolder) }
                    .thenReturn(messageService)
                funnyGuilds.onEnable()
            }
        } finally {
            field("logger").set(null, previousLogger)
        }

        verify(pluginManager).disablePlugin(funnyGuilds)
        verify(logger).warning(contains("CRITICAL USER IDENTITY CONFLICT"))
        verify(server, never()).shutdown()
        assertNull(field("dataPersistenceHandler").get(funnyGuilds))
        assertNull(field("invitationPersistenceHandler").get(funnyGuilds))
        assertEquals(0, userManager.countUsers())
        assertEquals("uuid: $first\nname: Steve\n", Files.readString(users.resolve("$first.yml")))
        assertEquals("uuid: $second\nname: steve\n", Files.readString(users.resolve("$second.yml")))
    }

    @Test
    fun `forced shutdown disables only FunnyGuilds and onDisable never saves partial data`() {
        val server = mock(Server::class.java)
        val pluginManager = mock(PluginManager::class.java)
        val dataModel = mock(DataModel::class.java)
        `when`(funnyGuilds.server).thenReturn(server)
        `when`(server.pluginManager).thenReturn(pluginManager)
        FunnyGuilds::class.java.getDeclaredField("dataModel").apply { isAccessible = true }.set(funnyGuilds, dataModel)
        doCallRealMethod().`when`(funnyGuilds).shutdown("Identity conflict")
        doCallRealMethod().`when`(funnyGuilds).onDisable()
        doCallRealMethod().`when`(funnyGuilds).onEnable()
        doAnswer { funnyGuilds.onDisable() }.`when`(pluginManager).disablePlugin(funnyGuilds)

        val loggerField = FunnyGuilds::class.java.getDeclaredField("logger").apply { isAccessible = true }
        val previousLogger = loggerField.get(null)
        loggerField.set(null, FunnyGuilds.getPluginLogger())
        try {
            funnyGuilds.shutdown("Identity conflict")
            // Subsequent lifecycle callbacks must also avoid loading or saving data.
            funnyGuilds.onEnable()
            funnyGuilds.onDisable()
        } finally {
            loggerField.set(null, previousLogger)
        }

        verify(pluginManager).disablePlugin(funnyGuilds)
        verify(server, never()).shutdown()
        verifyNoInteractions(dataModel)
    }

    private fun field(name: String) = FunnyGuilds::class.java.getDeclaredField(name).apply { isAccessible = true }
}
