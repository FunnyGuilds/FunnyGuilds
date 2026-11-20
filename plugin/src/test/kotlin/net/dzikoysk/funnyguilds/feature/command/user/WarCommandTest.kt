package net.dzikoysk.funnyguilds.feature.command.user

import net.dzikoysk.funnyguilds.config.sections.CommandsConfiguration
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class WarCommandTest {

    private val withdrawKeywords = CommandsConfiguration().warWithdrawKeywords

    @Test
    fun `defaults recognize zakoncz and end`() {
        assertTrue(WarCommand.isWithdrawKeyword(withdrawKeywords, "zakoncz"))
        assertTrue(WarCommand.isWithdrawKeyword(withdrawKeywords, "end"))
    }

    @Test
    fun `matches withdraw keywords case-insensitively`() {
        assertTrue(WarCommand.isWithdrawKeyword(withdrawKeywords, "ZAKONCZ"))
        assertTrue(WarCommand.isWithdrawKeyword(withdrawKeywords, "End"))
    }

    @Test
    fun `unknown second argument declares war`() {
        assertFalse(WarCommand.isWithdrawKeyword(withdrawKeywords, "stop"))
        assertFalse(WarCommand.isWithdrawKeyword(withdrawKeywords, "zakonczyc"))
    }

    @Test
    fun `empty keyword list disables withdrawal`() {
        assertFalse(WarCommand.isWithdrawKeyword(emptyList(), "zakoncz"))
    }

}
