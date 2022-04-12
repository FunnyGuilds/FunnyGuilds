package net.dzikoysk.funnyguilds.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NumberRangeTest {
    @Test
    fun `should parse integer full range of integers`() {
        val rangeMap = NumberRange.parseIntegerRange(listOf("0-* positive", "-100--1 negative", "-*--101 turbo-negative"), false)

        assertEquals("positive", NumberRange.inRangeToString(Int.MAX_VALUE, rangeMap))
        assertEquals("positive", NumberRange.inRangeToString(0, rangeMap))
        assertEquals("negative", NumberRange.inRangeToString(-1, rangeMap))
        assertEquals("negative", NumberRange.inRangeToString(-100, rangeMap))
        assertEquals("turbo-negative", NumberRange.inRangeToString(Int.MIN_VALUE, rangeMap))
    }

    @Test
    fun `should parse float full range of floats`() {
        val rangeMap = NumberRange.parseIntegerRange(listOf("*--7.5 super-negative", "-7.5-0 negative", "0-7.5 positive", "7.5-15 super-positive", "15-* turbo-positive"), false)

        assertEquals("super-negative", NumberRange.inRangeToString(-8, rangeMap))
        assertEquals("negative", NumberRange.inRangeToString(-7.5, rangeMap))
        assertEquals("positive", NumberRange.inRangeToString(0, rangeMap))
        assertEquals("super-positive", NumberRange.inRangeToString(7.5, rangeMap))
        assertEquals("turbo-positive", NumberRange.inRangeToString(15, rangeMap))
    }
}