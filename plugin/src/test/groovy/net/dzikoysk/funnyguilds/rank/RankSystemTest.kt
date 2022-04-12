package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RankSystemTest : FunnyGuildsSpec() {

    private var rankSystem = RankSystem.create(config)

    @Test
    fun `ELO rank system test 1000 vs 1000`() {
        val result = rankSystem.calculate(RankSystem.Type.ELO, 1000, 1000)

        assertEquals(16, result.attackerPoints)
        assertEquals(16, result.victimPoints)
    }

    @Test
    fun `ELO rank system test 1100 vs 1000`() {
        val result = rankSystem.calculate(RankSystem.Type.ELO, 1100, 1000)

        assertEquals(12, result.attackerPoints)
        assertEquals(12, result.victimPoints)
    }

    @Test
    fun `ELO rank system test 1000 vs 1200`() {
        val result = rankSystem.calculate(RankSystem.Type.ELO, 1000, 1200)

        assertEquals(24, result.attackerPoints)
        assertEquals(24, result.victimPoints)
    }

    @Test
    fun `ELO rank system test 1200 vs 800`() {
        val result = rankSystem.calculate(RankSystem.Type.ELO, 1200, 800)

        assertEquals(3, result.attackerPoints)
        assertEquals(3, result.victimPoints)
    }

    @Test
    fun `PERCENT rank system test`() {
        var result = rankSystem.calculate(RankSystem.Type.PERCENT, 1000, 1000)

        assertEquals(10, result.attackerPoints)
        assertEquals(10, result.victimPoints)

        result = rankSystem.calculate(RankSystem.Type.PERCENT, 1000, 1200)

        assertEquals(12, result.attackerPoints)
        assertEquals(12, result.victimPoints)
    }

    @Test
    fun `STATIC rank system test`() {
        val result = rankSystem.calculate(RankSystem.Type.STATIC, 1000, 1000)

        assertEquals(15, result.attackerPoints)
        assertEquals(10, result.victimPoints)
    }

}