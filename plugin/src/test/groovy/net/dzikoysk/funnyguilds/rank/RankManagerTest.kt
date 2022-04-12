package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Test
import panda.std.OptionAssertions.assertOptionEquals
import java.util.*

class RankManagerTest : FunnyGuildsSpec() {

    @Test
    fun `should_correctly_update_the_guilds_&_users_ranking`() {
        val rankRecalculationTask = RankRecalculationTask(funnyGuilds)

        val user1 = userManager.createFake(UUID.randomUUID(), "user1")
        val user2 = userManager.createFake(UUID.randomUUID(), "user2")
        val user3 = userManager.createFake(UUID.randomUUID(), "user3")

        val guild1 = guildManager.addGuild(Guild("OnlyPanda1", "OP1"))
        val guild2 = guildManager.addGuild(Guild("OnlyPanda2", "OP2"))
        val guild3 = guildManager.addGuild(Guild("OnlyPanda3", "OP3"))

        user1.rank.points = 200
        user2.rank.points = 150
        user3.rank.points = 100

        guild1.addMember(user1)
        guild2.addMember(user2)
        guild3.addMember(user3)

        rankRecalculationTask.run()

        assertOptionEquals(user1, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 1))
        assertOptionEquals(user2, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 2))
        assertOptionEquals(user3, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 3))
        assertOptionEquals(guild1, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 1))
        assertOptionEquals(guild2, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 2))
        assertOptionEquals(guild3, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 3))

        user1.rank.points = 100
        user2.rank.points = 150
        user3.rank.points = 200
        rankRecalculationTask.run()

        assertOptionEquals(user3, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 1))
        assertOptionEquals(user2, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 2))
        assertOptionEquals(user1, userRankManager.getUser(DefaultTops.USER_POINTS_TOP, 3))
        assertOptionEquals(guild3, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 1))
        assertOptionEquals(guild2, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 2))
        assertOptionEquals(guild1, guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, 3))
    }

}