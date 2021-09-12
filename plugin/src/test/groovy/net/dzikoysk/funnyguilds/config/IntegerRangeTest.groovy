package net.dzikoysk.funnyguilds.config

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.config.util.IntegerRange
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class IntegerRangeTest {

    @Test
    void 'should parse integer full range of integers' () {
        def rangeMap = IntegerRange.parseIntegerRange(['0-* positive', '-100--1 negative', '-*--101 turbo-negative' ], false)

        assertEquals 'positive', IntegerRange.inRangeToString(Integer.MAX_VALUE, rangeMap)
        assertEquals 'positive', IntegerRange.inRangeToString(0, rangeMap)
        assertEquals 'negative', IntegerRange.inRangeToString(-1, rangeMap)
        assertEquals 'negative', IntegerRange.inRangeToString(-100, rangeMap)
        assertEquals 'turbo-negative', IntegerRange.inRangeToString(Integer.MIN_VALUE, rangeMap)
    }

}
