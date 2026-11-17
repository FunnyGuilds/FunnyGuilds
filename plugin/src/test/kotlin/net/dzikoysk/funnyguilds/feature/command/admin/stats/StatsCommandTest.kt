package net.dzikoysk.funnyguilds.feature.command.admin.stats

import net.dzikoysk.funnyguilds.feature.command.InternalValidationException
import net.dzikoysk.funnyguilds.feature.command.admin.stats.StatsCommand.Operation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class StatsCommandTest {

    @Test
    fun `set resolves change relative to the current value`() {
        assertEquals(-5, StatsCommand.resolveChange(Operation.SET, 100, 105))
        assertEquals(97, StatsCommand.resolveChange(Operation.SET, 100, 3))
        assertEquals(0, StatsCommand.resolveChange(Operation.SET, 42, 42))
    }

    @Test
    fun `add resolves to the raw amount regardless of the current value`() {
        assertEquals(5, StatsCommand.resolveChange(Operation.ADD, 5, 105))
        assertEquals(5, StatsCommand.resolveChange(Operation.ADD, 5, 0))
    }

    @Test
    fun `remove resolves to the negated amount`() {
        assertEquals(-5, StatsCommand.resolveChange(Operation.REMOVE, 5, 105))
        assertEquals(-5, StatsCommand.resolveChange(Operation.REMOVE, 5, 0))
    }

    @Test
    fun `require operation matches keywords case-insensitively`() {
        assertEquals(Operation.SET, StatsCommand.requireOperation("set"))
        assertEquals(Operation.ADD, StatsCommand.requireOperation("ADD"))
        assertEquals(Operation.REMOVE, StatsCommand.requireOperation("Remove"))
    }

    @Test
    fun `require operation rejects unknown keywords`() {
        assertThrows(InternalValidationException::class.java) {
            StatsCommand.requireOperation("increment")
        }
    }

}
