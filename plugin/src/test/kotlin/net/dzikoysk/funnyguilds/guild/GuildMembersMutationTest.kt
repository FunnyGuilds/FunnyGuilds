package net.dzikoysk.funnyguilds.guild

import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.rank.RankService
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class GuildMembersMutationTest : FunnyGuildsSpec() {

    /**
     * Swaps the rank service wired into the manager for a spy of the real one, so tests can pin
     * ranking refreshes to the manager operations themselves.
     */
    private fun spyManagerRankService(): RankService {
        val rankServiceSpy = Mockito.spy(rankService)
        guildManager.setRankService(rankServiceSpy)
        return rankServiceSpy
    }

    @Test
    fun `setMembers should replace members and synchronize back references for a managed guild`() {
        val guild = Guild("guild", "TAG")
        guildManager.addGuild(guild)
        val keeper = userManager.createFake(UUID.randomUUID(), "keeper")
        val removed = userManager.createFake(UUID.randomUUID(), "removed")
        val added = userManager.createFake(UUID.randomUUID(), "added")
        guildManager.setMembers(guild, setOf(keeper, removed))
        val rankServiceSpy = spyManagerRankService()

        guildManager.setMembers(guild, setOf(keeper, added))

        assertTrue(guild.isMember(keeper))
        assertTrue(guild.isMember(added))
        assertFalse(guild.isMember(removed))
        assertEquals(Optional.of(guild), keeper.guild)
        assertEquals(Optional.of(guild), added.guild)
        assertEquals(Optional.empty<Guild>(), removed.guild)
        Mockito.verify(rankServiceSpy, Mockito.times(1)).refreshGuild(guild)
    }

    @Test
    fun `addMember should set the back reference and refresh on an effective join`() {
        val guild = Guild("guild", "TAG")
        guildManager.addGuild(guild)
        val added = userManager.createFake(UUID.randomUUID(), "added")
        val rankServiceSpy = spyManagerRankService()

        guildManager.addMember(guild, added)

        assertTrue(guild.isMember(added))
        assertEquals(Optional.of(guild), added.guild)
        Mockito.verify(rankServiceSpy, Mockito.times(1)).refreshGuild(guild)
    }

    @Test
    fun `addMember should not refresh when the membership does not change`() {
        val guild = Guild("guild", "TAG")
        guildManager.addGuild(guild)
        val member = userManager.createFake(UUID.randomUUID(), "member")
        guildManager.addMember(guild, member)
        val rankServiceSpy = spyManagerRankService()

        guildManager.addMember(guild, member)

        assertTrue(guild.isMember(member))
        Mockito.verify(rankServiceSpy, Mockito.never()).refreshGuild(guild)
    }

    @Test
    fun `removeMember should clear the back reference and refresh on an effective leave`() {
        val guild = Guild("guild", "TAG")
        guildManager.addGuild(guild)
        val member = userManager.createFake(UUID.randomUUID(), "member")
        guildManager.addMember(guild, member)
        val rankServiceSpy = spyManagerRankService()

        guildManager.removeMember(guild, member)

        assertFalse(guild.isMember(member))
        assertEquals(Optional.empty<Guild>(), member.guild)
        Mockito.verify(rankServiceSpy, Mockito.times(1)).refreshGuild(guild)
    }

    @Test
    fun `removeMember should not refresh when the user is not a member`() {
        val guild = Guild("guild", "TAG")
        guildManager.addGuild(guild)
        val outsider = userManager.createFake(UUID.randomUUID(), "outsider")
        val rankServiceSpy = spyManagerRankService()

        guildManager.removeMember(guild, outsider)

        assertFalse(guild.isMember(outsider))
        assertEquals(Optional.empty<Guild>(), outsider.guild)
        Mockito.verify(rankServiceSpy, Mockito.never()).refreshGuild(guild)
    }

    @Test
    fun `getMembers should return an unmodifiable live view`() {
        val guild = Guild("guild", "TAG")
        guildManager.addGuild(guild)
        val manager = userManager.createFake(UUID.randomUUID(), "manager")
        val raw = userManager.createFake(UUID.randomUUID(), "raw")
        val members = guild.members

        assertFailsWith<UnsupportedOperationException> { members.add(raw) }
        assertFailsWith<UnsupportedOperationException> { members.remove(raw) }

        // Coordinated manager operations stay visible through a previously captured view.
        guildManager.addMember(guild, manager)
        assertTrue(members.contains(manager))

        // Raw internal data mutations stay visible as well.
        assertTrue(guild.addMember(raw))
        assertTrue(members.contains(raw))
        assertTrue(guild.removeMember(raw))
        assertFalse(members.contains(raw))
    }

}
