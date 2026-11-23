package net.dzikoysk.funnyguilds.shared.bukkit

import org.bukkit.Material
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MaterialUtilsTest {

    @Test
    fun `missing empty and unknown configuration names remain unresolved`() {
        for (name in listOf(null, "", "not_a_material")) {
            assertTrue(MaterialUtils.matchMaterial(name).isEmpty, "Unexpected material for $name")
        }
    }

    @Test
    fun `configuration names retain Bukkit case and namespace support`() {
        for (name in listOf("DIAMOND", "diamond", "DiAmOnD", "minecraft:diamond")) {
            assertEquals(Material.DIAMOND, MaterialUtils.matchMaterial(name).orElseThrow())
        }
    }
}
