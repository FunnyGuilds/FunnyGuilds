package net.dzikoysk.funnyguilds.listener

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerLoginEvent
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.net.InetAddress
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlayerLoginIdentityTest : FunnyGuildsSpec() {

    @Test
    fun `login rejects an exact or case insensitive name collision without creating a user`() {
        userManager.createFake(UUID.randomUUID(), "Steve")
        for (name in listOf("Steve", "steve")) {
            val event = login(UUID.randomUUID(), name)
            listener().onLogin(event)
            assertEquals(PlayerLoginEvent.Result.KICK_OTHER, event.result)
            assertTrue(event.kickMessage.contains("UUID"))
            assertTrue(userManager.findByUuid(event.player.uniqueId).isEmpty)
            assertEquals(1, userManager.countUsers())
        }
    }

    @Test
    fun `same UUID may log in with its current name or a new available name`() {
        val user = userManager.createFake(UUID.randomUUID(), "Steve")
        for (name in listOf("Steve", "STEVE", "Steven")) {
            val event = login(user.uuid, name)
            listener().onLogin(event)
            assertEquals(PlayerLoginEvent.Result.ALLOWED, event.result)
        }
        // This is the rename performed by PlayerJoin once login has been accepted.
        userManager.updateUsername(user, "Steven")
        assertTrue(userManager.findByName("Steve", true).isEmpty)
    }

    @Test
    fun `login preserves an earlier rejection`() {
        val event = PlayerLoginEvent(mock(Player::class.java), "localhost", InetAddress.getLoopbackAddress())
        event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "Whitelist")
        listener().onLogin(event)
        assertEquals(PlayerLoginEvent.Result.KICK_WHITELIST, event.result)
        assertEquals("Whitelist", event.kickMessage)
    }

    private fun listener() = PlayerLogin().also {
        it.config = config
        it.userManager = userManager
        it.messageService = messageService
        it.logger = FunnyGuilds.getPluginLogger()
    }

    private fun login(uuid: UUID, name: String): PlayerLoginEvent {
        val player = mock(Player::class.java)
        `when`(player.uniqueId).thenReturn(uuid)
        `when`(player.name).thenReturn(name)
        return PlayerLoginEvent(player, "localhost", InetAddress.getLoopbackAddress())
    }
}
