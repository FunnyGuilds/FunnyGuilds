package net.dzikoysk.funnyguilds.rank

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.GuildRank
import net.dzikoysk.funnyguilds.guild.GuildUtils
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class RankManagerTest extends FunnyGuildsSpec {

    @Test
    void 'should correctly update the guilds and users ranking'() {
        def guild1 = new Guild("OnlyPanda1")
        def guild2 = new Guild("OnlyPanda2")
        def guild3 = new Guild("OnlyPanda3")
        def user1 = userManager.create(UUID.randomUUID(), "user1")
        def user2 = userManager.create(UUID.randomUUID(), "user2")
        def user3 = userManager.create(UUID.randomUUID(), "user3")

        guild1.rank = new GuildRank(guild1)
        guild1.addMember(user1)
        user1.rank.points = 200

        guild2.rank = new GuildRank(guild2)
        guild2.addMember(user2)
        user2.rank.points = 150

        guild3.rank = new GuildRank(guild3)
        guild3.addMember(user3)
        user3.rank.points = 100

        GuildUtils.addGuild(guild1)
        GuildUtils.addGuild(guild2)
        GuildUtils.addGuild(guild3)

        rankManager.recalculateUsersRank(userManager)
        rankManager.recalculateGuildsRank()

        assertEquals guild1, rankManager.getGuild(1)
        assertEquals guild2, rankManager.getGuild(2)
        assertEquals guild3, rankManager.getGuild(3)
        assertEquals user1, rankManager.getUser(1)
        assertEquals user2, rankManager.getUser(2)
        assertEquals user3, rankManager.getUser(3)

        user1.rank.points = 100
        user2.rank.points = 150
        user3.rank.points = 200

        rankManager.recalculateUsersRank(userManager)
        rankManager.recalculateGuildsRank()

        assertEquals guild3, rankManager.getGuild(1)
        assertEquals guild2, rankManager.getGuild(2)
        assertEquals guild1, rankManager.getGuild(3)
        assertEquals user3, rankManager.getUser(1)
        assertEquals user2, rankManager.getUser(2)
        assertEquals user1, rankManager.getUser(3)
    }

}
