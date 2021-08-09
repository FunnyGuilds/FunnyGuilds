package net.dzikoysk.funnyguilds.basic.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.user.UserManager
import net.dzikoysk.funnyguilds.util.IntegerRange
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class RankUtilsTest extends FunnyGuildsSpec {

    @Test
    void 'should parse rank with guild name'() {
        // given: a guild on top of the ranking
        def guild = new Guild('OnlyPanda')
        guild.setTag('OP')

        def user = new UserManager().create(UUID.randomUUID(), 'name')
        guild.addMember(user)
        RankManager.getInstance().update(guild)

        config.gtopPoints = ' {POINTS-FORMAT}'
        config.pointsFormat = [ new IntegerRange(0, Integer.MAX_VALUE) : '{POINTS}' ]

        // when: the GTOP placeholder is requested to parse
        def rank = RankUtils.parseRank(config, messages, RankManager.getInstance(), user, '{GTOP-1}')

        // then: the result should match the configured pattern
        assertEquals 'OP 1000', rank
    }

}
