package net.dzikoysk.funnyguilds.shared.bukkit

import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ResolvedItemDefinitionTest {

    @Test
    fun `unknown enchantments and malformed levels consistently report definition property and value`() {
        withEnchantmentRegistry { registry ->
            val sharpness = mock(Enchantment::class.java)
            `when`(registry.get(NamespacedKey.minecraft("sharpness"))).thenReturn(sharpness)
            for (value in listOf("unknown:2", "sharpness:invalid", "sharpness:", "sharpness:0", "sharpness:-1")) {
                val definition = GuildItemDefinition().also { it.enchants = listOf(value) }
                val error = assertFailsWith<IllegalArgumentException> {
                    definition.resolve("items.yml.guild-item-library.key")
                }
                assertEquals(IllegalArgumentException::class.java, error.javaClass)
                assertTrue(error.message!!.contains("items.yml.guild-item-library.key.enchants[0]"), error.message)
                assertTrue(error.message!!.contains(value), error.message)
            }
        }
    }

    @Test
    fun `matching and rendering reuse the same values without repeating registry lookups`() {
        withEnchantmentRegistry { registry ->
            val sharpness = mock(Enchantment::class.java)
            val enchantKey = NamespacedKey.minecraft("sharpness")
            `when`(registry.get(enchantKey)).thenReturn(sharpness)
            val definition = GuildItemDefinition("DIAMOND").also {
                it.name = "<gold>Key"
                it.lore = listOf("Fee")
                it.enchants = listOf("sharpness:2")
                it.flags = listOf("HIDE_ENCHANTS")
                it.customModelData = 7
            }
            definition.resolve("items.yml.guild-item-library.key")
            val resolved = definition.resolved
            val meta = mock(ItemMeta::class.java)
            `when`(meta.displayName()).thenReturn(resolved.name())
            `when`(meta.lore()).thenReturn(resolved.lore())
            `when`(meta.enchants).thenReturn(mapOf(sharpness to 2))
            `when`(meta.itemFlags).thenReturn(setOf(ItemFlag.HIDE_ENCHANTS))
            `when`(meta.hasCustomModelData()).thenReturn(true)
            `when`(meta.customModelData).thenReturn(7)
            val item = mock(ItemStack::class.java)
            `when`(item.type).thenReturn(Material.DIAMOND)
            `when`(item.amount).thenReturn(1)
            `when`(item.itemMeta).thenReturn(meta)

            repeat(3) {
                assertTrue(ItemMatcher(definition).test(item))
                definition.resolved.applyTo(meta)
                assertSame(resolved, definition.resolved)
            }

            verify(registry, times(1)).get(enchantKey)
            verify(meta, times(3)).displayName(resolved.name())
            verify(meta, times(3)).lore(resolved.lore())
            verify(meta, times(3)).addEnchant(sharpness, 2, true)
            verify(meta, times(3)).addItemFlags(ItemFlag.HIDE_ENCHANTS)
            verify(meta, times(3)).setCustomModelData(7)
        }
    }
}
