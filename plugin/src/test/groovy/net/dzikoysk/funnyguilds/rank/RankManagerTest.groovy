package net.dzikoysk.funnyguilds.rank

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class RankManagerTest extends FunnyGuildsSpec {

    @Test
    void 'should correctly update the guilds and users ranking'() {
        def rankRecalculationTask = new RankRecalculationTask(funnyGuilds);

        def user1 = userManager.create(UUID.randomUUID(), 'user1')
        def user2 = userManager.create(UUID.randomUUID(), 'user2')
        def user3 = userManager.create(UUID.randomUUID(), 'user3')
        def guild1 = guildManager.addGuild(new Guild('OnlyPanda1'))
        def guild2 = guildManager.addGuild(new Guild('OnlyPanda2'))
        def guild3 = guildManager.addGuild(new Guild('OnlyPanda3'))

        user1.rank.points = 200
        user2.rank.points = 150
        user3.rank.points = 100

        guild1.addMember(user1)
        guild2.addMember(user2)
        guild3.addMember(user3)

        rankRecalculationTask.run()

        assertEquals user1, rankManager.getUser(1).getOrNull()
        assertEquals user2, rankManager.getUser(2).getOrNull()
        assertEquals user3, rankManager.getUser(3).getOrNull()
        assertEquals guild1, rankManager.getGuild(1).getOrNull()
        assertEquals guild2, rankManager.getGuild(2).getOrNull()
        assertEquals guild3, rankManager.getGuild(3).getOrNull()

        user1.rank.points = 100
        user2.rank.points = 150
        user3.rank.points = 200

        rankRecalculationTask.run()

        assertEquals user3, rankManager.getUser(1).getOrNull()
        assertEquals user2, rankManager.getUser(2).getOrNull()
        assertEquals user1, rankManager.getUser(3).getOrNull()
        assertEquals guild3, rankManager.getGuild(1).getOrNull()
        assertEquals guild2, rankManager.getGuild(2).getOrNull()
        assertEquals guild1, rankManager.getGuild(3).getOrNull()
    }

}
