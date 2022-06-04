package net.dzikoysk.funnyguilds.shared

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FunnyFormatterTest {

    @Test
    fun `test single replacements`() {
        val formatter = FunnyFormatter()
                .register("{TEST}", "sth {TEST}")
                .register("{VALUE}", "x")

        Assertions.assertEquals("", formatter.format(null))
        Assertions.assertEquals("", formatter.format(""))
        Assertions.assertEquals("some text sth {TEST} x", formatter.format("some text {TEST} {VALUE}"))
        Assertions.assertEquals("some text {TEST2} {VALUE2}", formatter.format("some text {TEST2} {VALUE2}"))
    }

    @Test
    fun `test multiple replacements`() {
        val formatter = FunnyFormatter()
                .register("{TEST}", "sth {TEST}")
                .register("{VALUE}", "else {TEST}")
                .register("{TEST}", 1);

        Assertions.assertEquals("some text sth 1 else 1", formatter.format("some text {TEST} {VALUE}"))
    }

}