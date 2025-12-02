package net.dzikoysk.funnyguilds.shared

import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FunnyFormatterTest {

    @Test
    fun `test single replacements`() {
        val formatter = FunnyFormatter()
                .register("{TEST}", "sth {TEST}")
                .register("{VALUE}", "x")

        assertEquals("", formatter.replace(""))
        assertEquals("some text sth {TEST} x", formatter.replace("some text {TEST} {VALUE}"))
        assertEquals("some text {TEST2} {VALUE2}", formatter.replace("some text {TEST2} {VALUE2}"))
    }

    @Test
    fun `test multiple replacements`() {
        val formatter = FunnyFormatter()
                .register("{TEST}", "sth {TEST}")
                .register("{VALUE}", "else {TEST}")
                .register("{TEST}", 1)

        assertEquals("some text sth 1 else 1", formatter.replace("some text {TEST} {VALUE}"))
    }

}