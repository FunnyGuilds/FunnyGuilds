package net.dzikoysk.funnyguilds.shared.bukkit

import com.destroystokyo.paper.profile.PlayerProfile
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ItemMatcherTest {

    private fun item(material: Material, meta: ItemMeta? = null, amount: Int = 1): ItemStack =
        mock(ItemStack::class.java).also {
            `when`(it.type).thenReturn(material)
            `when`(it.amount).thenReturn(amount)
            `when`(it.itemMeta).thenReturn(meta)
        }

    @Test
    fun `material-only requirement ignores all metadata`() {
        val matcher = ItemMatcher(GuildItemDefinition("DIAMOND"))
        val meta = mock(ItemMeta::class.java)
        `when`(meta.displayName()).thenReturn(ItemUtils.coloredWithoutItalics("<red>Anything"))
        `when`(meta.customModelData).thenReturn(99)

        assertTrue(matcher.test(item(Material.DIAMOND)))
        assertTrue(matcher.test(item(Material.DIAMOND, meta)))
        assertFalse(matcher.test(item(Material.EMERALD, meta)))
    }

    @Test
    fun `name-only requirement accepts different materials and ignores other properties`() {
        val matcher = ItemMatcher(GuildItemDefinition().also { it.name = "<gold>Guild key" })
        val meta = mock(ItemMeta::class.java)
        `when`(meta.displayName()).thenReturn(ItemUtils.coloredWithoutItalics("<gold>Guild key"))
        `when`(meta.lore()).thenReturn(listOf(ItemUtils.coloredWithoutItalics("arbitrary lore")))
        `when`(meta.itemFlags).thenReturn(setOf(ItemFlag.HIDE_ENCHANTS))

        assertTrue(matcher.test(item(Material.DIAMOND, meta)))
        assertTrue(matcher.test(item(Material.STICK, meta)))
        `when`(meta.displayName()).thenReturn(ItemUtils.coloredWithoutItalics("<red>Guild key"))
        assertFalse(matcher.test(item(Material.STICK, meta)))
        assertFalse(matcher.test(item(Material.STICK)))
    }

    @Test
    fun `unset and legacy empty fields accept any nonempty item`() {
        val matcher = ItemMatcher(GuildItemDefinition().also {
            it.material = ""
            it.name = ""
            it.lore = emptyList()
            it.enchants = emptyList()
            it.flags = emptyList()
            it.skullOwner = ""
            it.armorColor = ""
        })
        assertTrue(matcher.test(item(Material.STICK)))
        assertTrue(matcher.test(item(Material.DIAMOND)))
        assertFalse(matcher.test(null))
        assertFalse(matcher.test(item(Material.AIR)))
        assertFalse(matcher.test(item(Material.CAVE_AIR)))
        assertFalse(matcher.test(item(Material.DIAMOND, amount = 0)))
    }

    @Test
    fun `configured lore flags and model data must each match exactly`() {
        val matcher = ItemMatcher(GuildItemDefinition().also {
            it.lore = listOf("<green>First", "", "Second")
            it.flags = listOf("hide_enchants")
            it.customModelData = 0
        })
        val meta = mock(ItemMeta::class.java)
        val lore = listOf(ItemUtils.coloredWithoutItalics("<green>First"), net.kyori.adventure.text.Component.empty(),
            ItemUtils.coloredWithoutItalics("Second"))
        `when`(meta.lore()).thenReturn(lore)
        `when`(meta.itemFlags).thenReturn(setOf(ItemFlag.HIDE_ENCHANTS))
        `when`(meta.hasCustomModelData()).thenReturn(true)
        `when`(meta.customModelData).thenReturn(0)
        val item = item(Material.STICK, meta)
        assertTrue(matcher.test(item))
        `when`(meta.hasCustomModelData()).thenReturn(false)
        assertFalse(matcher.test(item))
        `when`(meta.hasCustomModelData()).thenReturn(true)
        `when`(meta.itemFlags).thenReturn(setOf(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES))
        assertFalse(matcher.test(item))
        `when`(meta.itemFlags).thenReturn(setOf(ItemFlag.HIDE_ENCHANTS))
        `when`(meta.lore()).thenReturn(lore.reversed())
        assertFalse(matcher.test(item))
    }

    @Test
    fun `skull owner matches by name without comparing profile textures or type`() {
        val matcher = ItemMatcher(GuildItemDefinition().also { it.skullOwner = "Player" })
        val meta = mock(SkullMeta::class.java)
        val profile = mock(PlayerProfile::class.java)
        `when`(profile.name).thenReturn("pLaYeR")
        `when`(meta.playerProfile).thenReturn(profile)
        assertTrue(matcher.test(item(Material.PLAYER_HEAD, meta)))
        `when`(profile.name).thenReturn("Other")
        assertFalse(matcher.test(item(Material.PLAYER_HEAD, meta)))
        assertFalse(matcher.test(item(Material.STICK, mock(ItemMeta::class.java))))
    }

    @Test
    fun `armor color works without configuring a material`() {
        val matcher = ItemMatcher(GuildItemDefinition().also { it.armorColor = "12_34_56" })
        val meta = mock(LeatherArmorMeta::class.java)
        `when`(meta.color).thenReturn(Color.fromRGB(12, 34, 56))
        assertTrue(matcher.test(item(Material.LEATHER_BOOTS, meta)))
        assertTrue(matcher.test(item(Material.LEATHER_CHESTPLATE, meta)))
        `when`(meta.color).thenReturn(Color.RED)
        assertFalse(matcher.test(item(Material.LEATHER_BOOTS, meta)))
        assertFalse(matcher.test(item(Material.STICK, mock(ItemMeta::class.java))))
    }

    @Test
    fun `configured enchantments check the whole map and levels`() {
        withEnchantmentRegistry { registry ->
            val enchantment = mock(Enchantment::class.java)
            val extraEnchantment = mock(Enchantment::class.java)
            `when`(registry.get(org.bukkit.NamespacedKey.minecraft("sharpness"))).thenReturn(enchantment)
            val matcher = ItemMatcher(GuildItemDefinition().also { it.enchants = listOf("sharpness:2") })
            val meta = mock(ItemMeta::class.java)
            `when`(meta.enchants).thenReturn(mapOf(enchantment to 2))
            val item = item(Material.DIAMOND_SWORD, meta)
            assertTrue(matcher.test(item))
            `when`(meta.enchants).thenReturn(mapOf(enchantment to 1))
            assertFalse(matcher.test(item))
            `when`(meta.enchants).thenReturn(mapOf(enchantment to 2, extraEnchantment to 1))
            assertFalse(matcher.test(item))
        }
    }
}
