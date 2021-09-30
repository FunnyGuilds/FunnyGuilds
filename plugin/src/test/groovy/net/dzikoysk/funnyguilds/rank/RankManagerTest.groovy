package net.dzikoysk.funnyguilds.rank

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.GuildUtils
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class RankManagerTest extends FunnyGuildsSpec {

    @Test
    void 'should correctly update the guilds and users ranking'() {
        def rankRecalculationTask = new RankRecalculationTask(config, rankManager, userManager);

        def user1 = userManager.create(UUID.randomUUID(), "user1")
        def user2 = userManager.create(UUID.randomUUID(), "user2")
        def user3 = userManager.create(UUID.randomUUID(), "user3")
        def guild1 = new Guild("OnlyPanda1")
        def guild2 = new Guild("OnlyPanda2")
        def guild3 = new Guild("OnlyPanda3")

        user1.rank.points = 200
        user2.rank.points = 150
        user3.rank.points = 100

        guild1.addMember(user1)
        guild2.addMember(user2)
        guild3.addMember(user3)

        GuildUtils.addGuild(guild1)
        GuildUtils.addGuild(guild2)
        GuildUtils.addGuild(guild3)

        rankRecalculationTask.run()

        assertEquals user1, rankManager.getUser(1)
        assertEquals user2, rankManager.getUser(2)
        assertEquals user3, rankManager.getUser(3)
        assertEquals guild1, rankManager.getGuild(1)
        assertEquals guild2, rankManager.getGuild(2)
        assertEquals guild3, rankManager.getGuild(3)

        user1.rank.points = 100
        user2.rank.points = 150
        user3.rank.points = 200

        rankRecalculationTask.run()

        assertEquals user3, rankManager.getUser(1)
        assertEquals user2, rankManager.getUser(2)
        assertEquals user1, rankManager.getUser(3)
        assertEquals guild3, rankManager.getGuild(1)
        assertEquals guild2, rankManager.getGuild(2)
        assertEquals guild1, rankManager.getGuild(3)
    }

}
