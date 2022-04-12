package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.config.RangeFormatting
import net.dzikoysk.funnyguilds.config.RawString
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class RankUtilsTest : FunnyGuildsSpec() {

    @Test
    fun should_parse_rank_with_guild_name() {
        val rankRecalculationTask = RankRecalculationTask(funnyGuilds)

        // given: a guild on top of the ranking
        val guild = guildManager.addGuild(Guild("OnlyPanda", "OP"))

        val user = userManager.createFake(UUID.randomUUID(), "name")
        guild.addMember(user)

        rankRecalculationTask.run()

        config.gtopPoints = RawString(" {POINTS-FORMAT}")
        config.pointsFormat = ArrayList(Arrays.asList(RangeFormatting(0, Int.MAX_VALUE, "{POINTS}")))

        // when: the GTOP placeholder is requested to parse
        val rank = rankPlaceholdersService.formatRank("{GTOP-1}", user)

        // then: the result should match the configured pattern
        assertEquals("OP 1000", rank)
    }

}