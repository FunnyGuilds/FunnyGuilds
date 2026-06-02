package net.dzikoysk.funnyguilds.config

import net.dzikoysk.funnyguilds.config.ExplodeMaterialsScope.WildcardMode
import org.bukkit.Material
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExplodeMaterialsScopeTest {

    @Suppress("UNCHECKED_CAST")
    private fun parseScope(raw: Any?): ExplodeMaterialsScope =
        ExplodeMaterialsScope.parse(raw as? Map<String, Any>)

    @Test
    fun `none wildcard drops vanilla blocks and rolls only explicit materials`() {
        val scope = ExplodeMaterialsScope.parse(linkedMapOf<String, Any>("*" to "none", "water" to 33.0, "lava" to 33.0))

        assertTrue(scope.dropsVanillaBlocks())
        assertEquals(WildcardMode.NONE, scope.wildcardMode)
        assertEquals(33.0, scope.explosionChance(Material.WATER)!!, 1e-9)
        assertEquals(33.0, scope.explosionChance(Material.LAVA)!!, 1e-9)
        assertNull(scope.explosionChance(Material.STONE))
    }

    @Test
    fun `default wildcard keeps vanilla and rolls explicit materials only`() {
        val scope = ExplodeMaterialsScope.parse(linkedMapOf<String, Any>("*" to "default", "obsidian" to 20.0))

        assertFalse(scope.dropsVanillaBlocks())
        assertEquals(WildcardMode.DEFAULT, scope.wildcardMode)
        assertEquals(20.0, scope.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertNull(scope.explosionChance(Material.STONE))
    }

    @Test
    fun `numeric wildcard rolls every unlisted material`() {
        val scope = ExplodeMaterialsScope.parse(linkedMapOf<String, Any>("*" to 50.0, "obsidian" to 20.0))

        assertFalse(scope.dropsVanillaBlocks())
        assertEquals(WildcardMode.CHANCE, scope.wildcardMode)
        assertEquals(20.0, scope.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertEquals(50.0, scope.explosionChance(Material.STONE)!!, 1e-9)
    }

    @Test
    fun `missing wildcard defaults to vanilla`() {
        val scope = ExplodeMaterialsScope.parse(linkedMapOf<String, Any>("obsidian" to 20.0))

        assertEquals(WildcardMode.DEFAULT, scope.wildcardMode)
        assertNull(scope.explosionChance(Material.STONE))
    }

    @Test
    fun `negative chance disables a material`() {
        val scope = ExplodeMaterialsScope.parse(linkedMapOf<String, Any>("*" to "default", "water" to -1.0))

        assertNull(scope.explosionChance(Material.WATER))
    }

    @Test
    fun `string numbers are accepted`() {
        val scope = ExplodeMaterialsScope.parse(linkedMapOf<String, Any>("*" to "75", "obsidian" to "20.0"))

        assertEquals(WildcardMode.CHANCE, scope.wildcardMode)
        assertEquals(75.0, scope.explosionChance(Material.STONE)!!, 1e-9)
        assertEquals(20.0, scope.explosionChance(Material.OBSIDIAN)!!, 1e-9)
    }

    @Test
    fun `convertLegacy with flag disabled applies materials to both scopes`() {
        val nested = ExplodeMaterialsScope.convertLegacy(linkedMapOf<String, Any>("obsidian" to 20.0, "water" to 33.0), false)

        val guild = parseScope(nested["guild"])
        val global = parseScope(nested["global"])

        assertEquals(WildcardMode.DEFAULT, guild.wildcardMode)
        assertEquals(WildcardMode.DEFAULT, global.wildcardMode)
        assertEquals(20.0, guild.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertEquals(33.0, global.explosionChance(Material.WATER)!!, 1e-9)
    }

    @Test
    fun `convertLegacy with flag enabled keeps materials on guild and none on global`() {
        val nested = ExplodeMaterialsScope.convertLegacy(linkedMapOf<String, Any>("obsidian" to 20.0, "water" to 33.0), true)

        val guild = parseScope(nested["guild"])
        val global = parseScope(nested["global"])

        assertEquals(WildcardMode.DEFAULT, guild.wildcardMode)
        assertEquals(20.0, guild.explosionChance(Material.OBSIDIAN)!!, 1e-9)

        assertTrue(global.dropsVanillaBlocks())
        assertEquals(WildcardMode.NONE, global.wildcardMode)
        assertNull(global.explosionChance(Material.OBSIDIAN))
    }

    @Test
    fun `convertLegacy preserves explicit numeric wildcard`() {
        val nested = ExplodeMaterialsScope.convertLegacy(linkedMapOf<String, Any>("*" to 50.0, "obsidian" to 20.0), false)
        val global = parseScope(nested["global"])

        assertEquals(WildcardMode.CHANCE, global.wildcardMode)
        assertEquals(50.0, global.explosionChance(Material.STONE)!!, 1e-9)
    }
}
