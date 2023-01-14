package net.dzikoysk.funnyguilds.shared

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

class TimeUtilsTest : FunnyGuildsSpec() {

    @Test
    fun `should parse time`() {
        val durationOne = TimeUtils.parseTime("6d15h19m12s").toMillis()
        val durationTwo = TimeUtils.parseTime("2d12s").toMillis()

        assertEquals(durationOne, 573552 * 1000L)
        assertEquals(durationTwo, 172812 * 1000L)
    }

    @Test
    fun `test format time`() {
        val durationOne = Duration.ofDays(2).plusHours(15).plusMinutes(59).plusSeconds(1);
        val durationTwo = Duration.ofDays(2)
        val durationThree = Duration.ofSeconds(20)

        assertEquals("2 dni 15 godzin 59 minut 1 sekunda", TimeUtils.formatTime(durationOne))
        assertEquals("2 dni", TimeUtils.formatTime(durationTwo))
        assertEquals("20 sekund", TimeUtils.formatTime(durationThree))
    }

}