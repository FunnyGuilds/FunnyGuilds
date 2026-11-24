package net.dzikoysk.funnyguilds.feature.panel

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GuildRoleNameInputTest {

    @Test
    fun `only the next chat message is consumed and the callback runs on the scheduler`() {
        val plugin = mock(Plugin::class.java)
        val server = mock(Server::class.java)
        val scheduler = mock(BukkitScheduler::class.java)
        val player = mock(Player::class.java)
        val message = mock(AsyncChatEvent::class.java)
        val following = mock(AsyncChatEvent::class.java)
        `when`(plugin.server).thenReturn(server)
        `when`(server.scheduler).thenReturn(scheduler)
        `when`(player.uniqueId).thenReturn(UUID.randomUUID())
        `when`(message.player).thenReturn(player)
        `when`(message.originalMessage()).thenReturn(Component.text("  Dyplomata  "))
        `when`(following.player).thenReturn(player)

        val input = GuildRoleNameInput(plugin)
        var name: String? = null
        input.open(player, { name = it }, {})
        input.onChat(message)
        input.onChat(following)

        verify(message).setCancelled(true)
        verify(following).player
        verifyNoMoreInteractions(following)
        assertNull(name)

        val task = ArgumentCaptor.forClass(Runnable::class.java)
        verify(scheduler).runTask(org.mockito.ArgumentMatchers.eq(plugin), task.capture())
        task.value.run()
        assertEquals("Dyplomata", name)
    }
}
