package net.dzikoysk.funnyguilds.feature.panel

import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition
import net.dzikoysk.funnyguilds.feature.items.gui.GuiItemBuilder
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class GuildPanelIconTest {

    @Test
    fun `role and territory icon defaults resolve and both chain names select the same item`() {
        val screens = PanelConfiguration().screens
        val materials = screens.permissionMaterials.values + screens.expansionMaterials.values
        materials.forEach { assertNotNull(GuiItemBuilder.matchIconMaterial(it), it) }

        val chain = assertNotNull(GuiItemBuilder.matchIconMaterial("CHAIN"))
        assertSame(chain, GuiItemBuilder.matchIconMaterial("IRON_CHAIN"))
        assertSame(chain, GuiItemBuilder.matchIconMaterial("minecraft:iron_chain"))
        assertNull(GuiItemBuilder.matchIconMaterial("NONEXISTENT_ICON"))
    }

    @Test
    fun `an override constructs the final item directly and an unknown override keeps the template material`() {
        val definition = GuildItemDefinition("PAPER").apply {
            name = "{NAME}"
            lore = listOf("{STATE}")
        }
        val bucket = mock(ItemStack::class.java)
        val fallback = mock(ItemStack::class.java)
        mockStatic(ItemUtils::class.java).use { items ->
            items.`when`<ItemStack> { ItemUtils.toItemStack(definition, 1, Material.LAVA_BUCKET) }.thenReturn(bucket)
            items.`when`<ItemStack> { ItemUtils.toItemStack(definition, 1, Material.PAPER) }.thenReturn(fallback)
            val values = mapOf("NAME" to "Opróżnianie wiader", "STATE" to "Zezwól")

            assertSame(bucket, GuiItemBuilder.buildSimple(definition, "LAVA_BUCKET"))
            assertSame(fallback, GuiItemBuilder.buildSimple(definition, "NONEXISTENT_ICON"))
        }

        verify(bucket, never()).withType(Material.LAVA_BUCKET)
        assertEquals("PAPER", definition.material)
    }
}
