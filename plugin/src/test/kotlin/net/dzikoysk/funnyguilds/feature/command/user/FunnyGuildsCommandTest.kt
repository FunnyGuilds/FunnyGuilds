package net.dzikoysk.funnyguilds.feature.command.user

import dev.peri.yetanothermessageslibrary.message.Sendable
import dev.peri.yetanothermessageslibrary.message.SendableMessage
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.FunnyGuildsLogger
import net.dzikoysk.funnyguilds.config.message.FunnyMessageDispatcher
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration
import net.dzikoysk.funnyguilds.config.message.MessageService
import net.dzikoysk.funnyguilds.data.DataPersistenceHandler
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerManager
import org.bukkit.command.CommandSender
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.function.Consumer
import java.util.function.Function

class FunnyGuildsCommandTest {

    private val itemsError = "items.yml.guild-item-library.key.material: Invalid material: TYPO"

    private fun prepareDispatcher(messageService: MessageService): FunnyMessageDispatcher {
        val dispatcher = mock(FunnyMessageDispatcher::class.java)
        `when`(dispatcher.receiver(any(CommandSender::class.java))).thenReturn(dispatcher)
        `when`(dispatcher.with(any(String::class.java), any(Any::class.java))).thenReturn(dispatcher)
        `when`(messageService.getMessage(any<Function<MessageConfiguration, Sendable?>>())).thenReturn(dispatcher)
        return dispatcher
    }

    private fun preparePlugin(messageService: MessageService, itemsError: String?): FunnyGuilds {
        val plugin = mock(FunnyGuilds::class.java)
        `when`(plugin.messageService).thenReturn(messageService)
        `when`(plugin.dataPersistenceHandler).thenReturn(mock(DataPersistenceHandler::class.java, RETURNS_DEEP_STUBS))
        `when`(plugin.dynamicListenerManager).thenReturn(mock(DynamicListenerManager::class.java, RETURNS_DEEP_STUBS))
        if (itemsError != null) {
            doAnswer { invocation ->
                invocation.getArgument<Consumer<String>>(0).accept(itemsError)
                null
            }.`when`(plugin).reloadConfiguration(any<Consumer<String>>())
        }
        return plugin
    }

    @Suppress("UNCHECKED_CAST")
    private fun sentMessageKeys(messageService: MessageService, times: Int): List<String> {
        val config = MessageConfiguration()
        val captor: ArgumentCaptor<Function<MessageConfiguration, Sendable?>> =
            ArgumentCaptor.forClass(Function::class.java as Class<Function<MessageConfiguration, Sendable?>>)
        verify(messageService, times(times)).getMessage(captor.capture())
        return captor.allValues.map { message ->
            MessageConfiguration::class.java.declaredFields
                .firstOrNull { it.type == SendableMessage::class.java && it.get(config) === message.apply(config) }
                ?.name
                ?: "unknown"
        }
    }

    @Test
    fun `valid reload reports the usual completion message`() {
        val messageService = mock(MessageService::class.java)
        val dispatcher = prepareDispatcher(messageService)
        val plugin = preparePlugin(messageService, null)

        FunnyGuildsCommand.ReloadAsyncTask(plugin, mock(CommandSender::class.java)).execute()

        assertEquals(listOf("reloadTime"), sentMessageKeys(messageService, 1))
        verify(dispatcher).with(eq("{TIME}"), any(Any::class.java))
        verify(dispatcher, never()).with(eq("{ERROR}"), any(Any::class.java))
    }

    @Test
    fun `rejected items config followed by successful reload reports partial completion`() {
        val messageService = mock(MessageService::class.java)
        val dispatcher = prepareDispatcher(messageService)
        val plugin = preparePlugin(messageService, itemsError)

        FunnyGuildsCommand.ReloadAsyncTask(plugin, mock(CommandSender::class.java)).execute()

        assertEquals(listOf("reloadItemsError", "reloadItemsPartial"), sentMessageKeys(messageService, 2))
        verify(dispatcher, times(2)).with(eq("{ERROR}"), any(Any::class.java))
        verify(dispatcher).with(eq("{TIME}"), any(Any::class.java))
    }

    @Test
    fun `failed reload step suppresses the completion message`() {
        val messageService = mock(MessageService::class.java)
        val dispatcher = prepareDispatcher(messageService)
        val plugin = mock(FunnyGuilds::class.java)
        `when`(plugin.messageService).thenReturn(messageService)
        val dataPersistenceHandler = mock(DataPersistenceHandler::class.java)
        `when`(plugin.dataPersistenceHandler).thenReturn(dataPersistenceHandler)
        doThrow(IllegalStateException("boom")).`when`(dataPersistenceHandler).reloadHandler()

        mockStatic(FunnyGuilds::class.java).use {
            it.`when`<FunnyGuildsLogger> { FunnyGuilds.getPluginLogger() }.thenReturn(mock(FunnyGuildsLogger::class.java))
            FunnyGuildsCommand.ReloadAsyncTask(plugin, mock(CommandSender::class.java)).execute()
        }

        verify(dispatcher, never()).send()
        sentMessageKeys(messageService, 0)
    }

}
