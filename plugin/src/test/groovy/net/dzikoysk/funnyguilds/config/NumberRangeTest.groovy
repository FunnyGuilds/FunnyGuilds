package net.dzikoysk.funnyguilds.config

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class NumberRangeTest {

    @Test
    void 'should parse integer full range of integers' () {
        def rangeMap = NumberRange.parseIntegerRange(['0-* positive', '-100--1 negative', '-*--101 turbo-negative' ], false)

        assertEquals 'positive', NumberRange.inRangeToString(Integer.MAX_VALUE, rangeMap)
        assertEquals 'positive', NumberRange.inRangeToString(0, rangeMap)
        assertEquals 'negative', NumberRange.inRangeToString(-1, rangeMap)
        assertEquals 'negative', NumberRange.inRangeToString(-100, rangeMap)
        assertEquals 'turbo-negative', NumberRange.inRangeToString(Integer.MIN_VALUE, rangeMap)
    }

    @Test
    void 'should parse float full range of floats' () {
        def rangeMap = NumberRange.parseIntegerRange(['*--7.5 super-negative', '-7.5-0 negative', '0-7.5 positive', '7.5-15 super-positive', '15-* turbo-positive' ], false)

        assertEquals 'super-negative', NumberRange.inRangeToString(-8, rangeMap)
        assertEquals 'negative', NumberRange.inRangeToString(-7.5, rangeMap)
        assertEquals 'positive', NumberRange.inRangeToString(0, rangeMap)
        assertEquals 'super-positive', NumberRange.inRangeToString(7.5, rangeMap)
        assertEquals 'turbo-positive', NumberRange.inRangeToString(15, rangeMap)
    }

}
