package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.UUID

class RankShimsTest : FunnyGuildsSpec() {

    @Test
    fun `should correctly update the guilds & users ranking`() {

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


        assertEquals(user1, rankService.getUserRankTracker(DefaultTops.USER_POINTS_TOP).get().findEntityByPosition(1).orElse(null))
        assertEquals(user2, rankService.getUserRankTracker(DefaultTops.USER_POINTS_TOP).get().findEntityByPosition(2).orElse(null))
        assertEquals(user3, rankService.getUserRankTracker(DefaultTops.USER_POINTS_TOP).get().findEntityByPosition(3).orElse(null))
        assertEquals(guild1, rankService.getGuildRankTracker(DefaultTops.GUILD_AVG_POINTS_TOP).get().findEntityByPosition(1).orElse(null))
        assertEquals(guild2, rankService.getGuildRankTracker(DefaultTops.GUILD_AVG_POINTS_TOP).get().findEntityByPosition(2).orElse(null))
        assertEquals(guild3, rankService.getGuildRankTracker(DefaultTops.GUILD_AVG_POINTS_TOP).get().findEntityByPosition(3).orElse(null))

        user1.rank.points = 100
        user2.rank.points = 150
        user3.rank.points = 200

        assertEquals(user3, rankService.getUserRankTracker(DefaultTops.USER_POINTS_TOP).get().findEntityByPosition(1).orElse(null))
        assertEquals(user2, rankService.getUserRankTracker(DefaultTops.USER_POINTS_TOP).get().findEntityByPosition(2).orElse(null))
        assertEquals(user1, rankService.getUserRankTracker(DefaultTops.USER_POINTS_TOP).get().findEntityByPosition(3).orElse(null))
        assertEquals(guild3, rankService.getGuildRankTracker(DefaultTops.GUILD_AVG_POINTS_TOP).get().findEntityByPosition(1).orElse(null))
        assertEquals(guild2, rankService.getGuildRankTracker(DefaultTops.GUILD_AVG_POINTS_TOP).get().findEntityByPosition(2).orElse(null))
        assertEquals(guild1, rankService.getGuildRankTracker(DefaultTops.GUILD_AVG_POINTS_TOP).get().findEntityByPosition(3).orElse(null))
    }

}
