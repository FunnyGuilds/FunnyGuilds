package net.dzikoysk.funnyguilds.config

import net.dzikoysk.funnyguilds.config.ExplosionControlConfiguration.Scope
import org.bukkit.Material
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExplosionControlScopeTest {

    private fun scope(default: Double, vararg materials: Pair<String, Double>): Scope {
        val scope = Scope.of(3, default, linkedMapOf(*materials))
        scope.loadProcessedProperties()
        return scope
    }

    @Test
    fun `default 0 protects vanilla blocks and destroys only the listed materials`() {
        val scope = scope(0.0, "water" to 33.0, "lava" to 33.0)

        assertTrue(scope.protectsVanillaBlocks())
        assertEquals(33.0, scope.explosionChance(Material.WATER)!!, 1e-9)
        assertEquals(33.0, scope.explosionChance(Material.LAVA)!!, 1e-9)
        assertNull(scope.explosionChance(Material.STONE))
    }

    @Test
    fun `default -1 keeps vanilla and destroys only the listed materials`() {
        val scope = scope(-1.0, "obsidian" to 20.0)

        assertFalse(scope.protectsVanillaBlocks())
        assertEquals(20.0, scope.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertNull(scope.explosionChance(Material.STONE))
    }

    @Test
    fun `positive default destroys every other material`() {
        val scope = scope(50.0, "obsidian" to 20.0)

        assertFalse(scope.protectsVanillaBlocks())
        assertEquals(20.0, scope.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertEquals(50.0, scope.explosionChance(Material.STONE)!!, 1e-9)
    }

    @Test
    fun `negative override disables a material even when the default is positive`() {
        val scope = scope(50.0, "water" to -1.0)

        assertNull(scope.explosionChance(Material.WATER))
        assertEquals(50.0, scope.explosionChance(Material.STONE)!!, 1e-9)
    }
}
