package net.dzikoysk.funnyguilds.rank

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Test

import static panda.std.OptionAssertions.assertOptionEquals

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

        assertOptionEquals user1, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 1)
        assertOptionEquals user2, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 2)
        assertOptionEquals user3, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 3)
        assertOptionEquals guild1, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 1)
        assertOptionEquals guild2, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 2)
        assertOptionEquals guild3, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 3)

        user1.rank.points = 100
        user2.rank.points = 150
        user3.rank.points = 200

        rankRecalculationTask.run()

        assertOptionEquals user3, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 1)
        assertOptionEquals user2, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 2)
        assertOptionEquals user1, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 3)
        assertOptionEquals guild3, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 1)
        assertOptionEquals guild2, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 2)
        assertOptionEquals guild1, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 3)
    }

}
