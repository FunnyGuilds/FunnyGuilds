package net.dzikoysk.funnyguilds.config

import org.bukkit.Material
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExplodeMaterialsScopeTest {

    private fun scope(default: Any?, vararg overrides: Pair<String, Any>): ExplodeMaterialsScope {
        val raw = linkedMapOf<String, Any>("overrides" to linkedMapOf(*overrides))
        if (default != null) {
            raw["default"] = default
        }
        return ExplodeMaterialsScope.parse(raw)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseScope(raw: Any?): ExplodeMaterialsScope =
        ExplodeMaterialsScope.parse(raw as? Map<String, Any>)

    @Test
    fun `none default drops vanilla blocks and rolls only overridden materials`() {
        val scope = scope("none", "water" to 33.0, "lava" to 33.0)

        assertTrue(scope.dropsVanillaBlocks())
        assertEquals(33.0, scope.explosionChance(Material.WATER)!!, 1e-9)
        assertEquals(33.0, scope.explosionChance(Material.LAVA)!!, 1e-9)
        assertNull(scope.explosionChance(Material.STONE))
    }

    @Test
    fun `vanilla default keeps vanilla and rolls overridden materials only`() {
        val scope = scope("vanilla", "obsidian" to 20.0)

        assertFalse(scope.dropsVanillaBlocks())
        assertEquals(20.0, scope.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertNull(scope.explosionChance(Material.STONE))
    }

    @Test
    fun `numeric default rolls every other material`() {
        val scope = scope(50.0, "obsidian" to 20.0)

        assertFalse(scope.dropsVanillaBlocks())
        assertEquals(20.0, scope.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertEquals(50.0, scope.explosionChance(Material.STONE)!!, 1e-9)
    }

    @Test
    fun `missing default falls back to vanilla`() {
        val scope = scope(null, "obsidian" to 20.0)

        assertFalse(scope.dropsVanillaBlocks())
        assertNull(scope.explosionChance(Material.STONE))
    }

    @Test
    fun `negative chance disables a material`() {
        val scope = scope("vanilla", "water" to -1.0)

        assertNull(scope.explosionChance(Material.WATER))
    }

    @Test
    fun `string numbers are accepted`() {
        val scope = scope("75", "obsidian" to "20.0")

        assertEquals(75.0, scope.explosionChance(Material.STONE)!!, 1e-9)
        assertEquals(20.0, scope.explosionChance(Material.OBSIDIAN)!!, 1e-9)
    }

    @Test
    fun `convertLegacy with flag disabled applies materials to both scopes`() {
        val nested = ExplodeMaterialsScope.convertLegacy(linkedMapOf<String, Any>("obsidian" to 20.0, "water" to 33.0), false)

        val guild = parseScope(nested["guild"])
        val global = parseScope(nested["global"])

        assertFalse(guild.dropsVanillaBlocks())
        assertFalse(global.dropsVanillaBlocks())
        assertEquals(20.0, guild.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertEquals(33.0, global.explosionChance(Material.WATER)!!, 1e-9)
    }

    @Test
    fun `convertLegacy with flag enabled keeps materials on guild and none on global`() {
        val nested = ExplodeMaterialsScope.convertLegacy(linkedMapOf<String, Any>("obsidian" to 20.0, "water" to 33.0), true)

        val guild = parseScope(nested["guild"])
        val global = parseScope(nested["global"])

        assertFalse(guild.dropsVanillaBlocks())
        assertEquals(20.0, guild.explosionChance(Material.OBSIDIAN)!!, 1e-9)

        assertTrue(global.dropsVanillaBlocks())
        assertNull(global.explosionChance(Material.OBSIDIAN))
    }

    @Test
    fun `convertLegacy preserves the legacy numeric wildcard as the default`() {
        val nested = ExplodeMaterialsScope.convertLegacy(linkedMapOf<String, Any>("*" to 50.0, "obsidian" to 20.0), false)
        val global = parseScope(nested["global"])

        assertEquals(50.0, global.explosionChance(Material.STONE)!!, 1e-9)
        assertEquals(20.0, global.explosionChance(Material.OBSIDIAN)!!, 1e-9)
    }
}
