package net.dzikoysk.funnyguilds.config

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class NumberRangeTest {

    @Test
    void 'should parse number full range of numbers' () {
        def rangeMap = NumberRange.parseIntegerRange(['0-* positive', '-100--1 negative', '-*--101 turbo-negative' ], false)

        assertEquals 'positive', NumberRange.inRangeToString(Integer.MAX_VALUE, rangeMap)
        assertEquals 'positive', NumberRange.inRangeToString(0, rangeMap)
        assertEquals 'negative', NumberRange.inRangeToString(-1, rangeMap)
        assertEquals 'negative', NumberRange.inRangeToString(-100, rangeMap)
        assertEquals 'turbo-negative', NumberRange.inRangeToString(Integer.MIN_VALUE, rangeMap)
    }

}
