package net.dzikoysk.funnyguilds.shared

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test

class FunnyStringUtilsTest {

    @Test
    fun `test join`() {
        val manyItems = listOf("a", "b", "c", "d")

        assertEquals("a", FunnyStringUtils.join(listOf("a")))
        assertEquals("a,b,c,d", FunnyStringUtils.join(manyItems))
        assertEquals("a, b, c, d", FunnyStringUtils.join(manyItems, true))
        assertEquals("a:b:c:d", FunnyStringUtils.join(manyItems, ":"))
    }

    @Test
    fun `test list from string`() {
        assertIterableEquals(listOf("a"), FunnyStringUtils.fromString("a"))
        assertIterableEquals(listOf("a", "b", "c", "d"), FunnyStringUtils.fromString("a,b,c,d"))
        assertIterableEquals(listOf("a", " b", " c", " d"), FunnyStringUtils.fromString("a, b, c, d"))
        assertIterableEquals(listOf("a;b;c;d"), FunnyStringUtils.fromString("a;b;c;d"))
    }

    @Test
    fun `test digit append`() {
        assertEquals("10", FunnyStringUtils.appendDigit(10))
        assertEquals("09", FunnyStringUtils.appendDigit(9))
        assertEquals("10", FunnyStringUtils.appendDigit("10"))
        assertEquals("09", FunnyStringUtils.appendDigit("9"))
        assertEquals("0x", FunnyStringUtils.appendDigit("x"))
    }

    @Test
    fun `test percent`() {
        assertEquals("33.3", FunnyStringUtils.getPercent(1.0, 3.0))
        assertEquals("33.3", FunnyStringUtils.getPercent(1.0 / 3.0))
        assertEquals("0.1", FunnyStringUtils.getPercent(1.0 / 1100.0))
    }

}
