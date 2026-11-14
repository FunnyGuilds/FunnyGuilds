package net.dzikoysk.funnyguilds.user

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class UserManagerTest : FunnyGuildsSpec() {

    @Test
    fun `case variants with different UUIDs remain distinct even with startup validation enabled`() {
        config.preventUuidConflicts = true
        val original = userManager.createFake(UUID.randomUUID(), "Steve")
        val otherUuid = UUID.randomUUID()

        val other = userManager.createFake(otherUuid, "steve")

        assertEquals(2, userManager.countUsers())
        assertSame(original, userManager.findByName("Steve").get())
        assertSame(other, userManager.findByName("steve").get())
        assertSame(other, userManager.findByName("steve", true).get())
        assertSame(other, userManager.findByUuid(otherUuid).get())
    }

    @Test
    fun `rename to a previously used name preserves both UUID accounts`() {
        val steve = userManager.createFake(UUID.randomUUID(), "Steve")
        val alex = userManager.createFake(UUID.randomUUID(), "Alex")
        alex.markUnchanged()

        userManager.updateUsername(alex, "STEVE")

        assertEquals("STEVE", alex.name)
        assertTrue(alex.wasChanged())
        assertSame(steve, userManager.findByName("Steve").get())
        assertSame(alex, userManager.findByName("STEVE").get())
        assertTrue(userManager.findByName("Alex").isEmpty)
        assertSame(alex, userManager.findByUuid(alex.uuid).get())
    }

    @Test
    fun `rename releases the old name and preserves guild membership and exact lookup semantics`() {
        val user = userManager.createFake(UUID.randomUUID(), "Steve")
        val guild = guildManager.addGuild(net.dzikoysk.funnyguilds.guild.Guild("Test", "TST"))
        guild.setOwner(user)
        guild.addMember(user)
        user.markUnchanged()

        userManager.updateUsername(user, "Steven")

        assertTrue(userManager.findByName("Steve", true).isEmpty)
        assertTrue(userManager.findByName("steven").isEmpty)
        assertSame(user, userManager.findByName("steven", true).get())
        assertSame(user, userManager.findByUuid(user.uuid).get())
        assertSame(user, guild.owner)
        assertTrue(guild.members.contains(user))
        assertTrue(user.wasChanged())
        val newOwner = userManager.createFake(UUID.randomUUID(), "Steve")
        assertSame(newOwner, userManager.findByName("Steve").get())
    }

    @Test
    fun `case only rename and remove update the normalized index`() {
        val user = userManager.createFake(UUID.randomUUID(), "Steve")
        userManager.updateUsername(user, "STEVE")
        assertTrue(userManager.findByName("Steve").isEmpty)
        assertSame(user, userManager.findByName("STEVE").get())
        assertSame(user, userManager.findByName("steve", true).get())

        userManager.removeUser(user)
        assertTrue(userManager.findByName("steve", true).isEmpty)
        assertTrue(userManager.findByUuid(user.uuid).isEmpty)
    }

    @Test
    fun `duplicate UUID cannot replace a referenced user and stale remove cannot delete the replacement`() {
        val user = userManager.createFake(UUID.randomUUID(), "Steve")
        val duplicate = User(user.uuid, "Alex", FakeUserProfile.offline())
        val guild = guildManager.addGuild(Guild("Test", "TST"))
        guild.setOwner(user)

        assertSame(user, userManager.createFake(user.uuid, "Steve"))
        userManager.addUser(user)
        assertFailsWith<IllegalStateException> { userManager.createFake(user.uuid, "Alex") }
        val failure = assertFailsWith<IllegalStateException> {
            userManager.addUser(duplicate)
            guild.setOwner(duplicate)
        }
        assertContains(failure.message!!, user.uuid.toString())
        assertSame(user, guild.owner)
        assertEquals(1, userManager.countUsers())
        assertSame(user, userManager.findByUuid(user.uuid).get())
        assertSame(user, userManager.findByName("Steve").get())
        assertTrue(userManager.findByName("Alex").isEmpty)

        userManager.removeUser(user)
        val replacement = userManager.createFake(user.uuid, "Steve")
        userManager.removeUser(user)
        assertFailsWith<IllegalStateException> { userManager.updateUsername(user, "Alex") }
        assertSame(replacement, userManager.findByName("Steve").get())
        assertSame(replacement, userManager.findByUuid(user.uuid).get())
    }

    @Test
    fun `concurrent registration permits distinct offline mode names`() {
        // Construct on the test thread because FunnyGuildsSpec's static Mockito mocks are thread local.
        val first = User(UUID.randomUUID(), "Steve", FakeUserProfile.offline())
        val second = User(UUID.randomUUID(), "steve", FakeUserProfile.offline())
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)
        try {
            val attempts = listOf(first, second).map { user ->
                executor.submit<Boolean> {
                    start.await()
                    try {
                        userManager.addUser(user)
                        true
                    } catch (_: IllegalStateException) {
                        false
                    }
                }
            }
            start.countDown()
            assertEquals(2, attempts.count { it.get(5, TimeUnit.SECONDS) })
            assertEquals(2, userManager.countUsers())
            assertSame(first, userManager.findByName("Steve").get())
            assertSame(second, userManager.findByName("steve").get())
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun `findByPlayer should ignore NPC entities marked with a version-2 uuid`() {
        val npcUuid = UUID(0x2000L, 0L)
        assertEquals(2, npcUuid.version(), "test fixture must be a version-2 (NPC) uuid")

        val npc = Mockito.mock(Player::class.java)
        Mockito.`when`(npc.uniqueId).thenReturn(npcUuid)

        assertTrue(userManager.findByPlayer(npc).isEmpty, "NPC entities must not resolve to a user")
    }

    @Test
    fun `a new account may use an old name before its previous owner rejoins with a new name`() {
        val previousOwner = userManager.createFake(UUID.randomUUID(), "Steve")
        val newOwner = userManager.createFake(UUID.randomUUID(), "Steve")
        assertSame(previousOwner, userManager.findByUuid(previousOwner.uuid).get())
        assertSame(newOwner, userManager.findByName("Steve").get())
        userManager.updateUsername(previousOwner, "Steven")
        assertSame(newOwner, userManager.findByName("Steve").get())
        assertSame(previousOwner, userManager.findByName("Steven").get())
        assertEquals(2, userManager.countUsers())
    }

    @Test
    fun `removing a previous owner does not remove a reused name from its new owner`() {
        val previousOwner = userManager.createFake(UUID.randomUUID(), "Steve")
        val newOwner = userManager.createFake(UUID.randomUUID(), "Steve")
        userManager.removeUser(previousOwner)
        assertSame(newOwner, userManager.findByName("Steve").get())
        assertSame(newOwner, userManager.findByUuid(newOwner.uuid).get())
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
