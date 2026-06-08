package net.dzikoysk.funnyguilds.user

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserManagerTest : FunnyGuildsSpec() {

    @Test
    fun `findByPlayer should ignore NPC entities marked with a version-2 uuid`() {
        val npcUuid = UUID(0x2000L, 0L)
        assertEquals(2, npcUuid.version(), "test fixture must be a version-2 (NPC) uuid")

        val npc = Mockito.mock(Player::class.java)
        Mockito.`when`(npc.uniqueId).thenReturn(npcUuid)

        assertTrue(userManager.findByPlayer(npc).isEmpty, "NPC entities must not resolve to a user")
    }

    @Test
    fun `findByPlayer should resolve a registered real player`() {
        val uuid = UUID.randomUUID()
        val user = userManager.create(uuid, "realPlayer", FakeUserProfile.offline())

        val player = Mockito.mock(Player::class.java)
        Mockito.`when`(player.uniqueId).thenReturn(uuid)

        val result = userManager.findByPlayer(player)

        assertTrue(result.isPresent, "a registered real player must resolve to a user")
        assertEquals(user, result.get())
    }

    @Test
    fun `findByPlayer should return empty for an unknown real player`() {
        val player = Mockito.mock(Player::class.java)
        Mockito.`when`(player.uniqueId).thenReturn(UUID.randomUUID())

        assertTrue(userManager.findByPlayer(player).isEmpty, "an unregistered player must not be created on lookup")
    }

}
