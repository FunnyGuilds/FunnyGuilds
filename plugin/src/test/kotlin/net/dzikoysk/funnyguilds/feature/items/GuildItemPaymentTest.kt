package net.dzikoysk.funnyguilds.feature.items

import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.ItemMeta
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.nullable
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class GuildItemPaymentTest {

    private fun stack(material: Material, amount: Int, meta: ItemMeta? = null): ItemStack {
        val item = mock(ItemStack::class.java)
        var currentAmount = amount
        `when`(item.type).thenReturn(material)
        `when`(item.amount).thenAnswer { currentAmount }
        `when`(item.itemMeta).thenReturn(meta)
        `when`(item.clone()).thenAnswer { stack(material, currentAmount, meta) }
        doAnswer { currentAmount = it.getArgument(0); null }.`when`(item).setAmount(anyInt())
        return item
    }

    private fun inventory(contents: Array<ItemStack?>): PlayerInventory = mock(PlayerInventory::class.java).also { inventory ->
        `when`(inventory.storageContents).thenAnswer { contents.copyOf() }
        `when`(inventory.getItem(anyInt())).thenAnswer { contents[it.getArgument<Int>(0)] }
        doAnswer {
            contents[it.getArgument<Int>(0)] = it.getArgument(1)
            null
        }.`when`(inventory).setItem(anyInt(), nullable(ItemStack::class.java))
    }

    private fun config(vararg definitions: Pair<String, GuildItemDefinition>): ItemsConfiguration =
        ItemsConfiguration().also {
            it.guildItemLibrary = linkedMapOf(*definitions)
            it.guildToggleLibrary = emptyMap()
        }

    private fun set(vararg requirements: Pair<String, Int>): GuildItemSet = GuildItemSet().also {
        it.items = linkedMapOf(*requirements)
        it.requirements.moneyEnabled = false
        it.requirements.levelEnabled = false
        it.requirements.rankEnabled = false
    }

    @Test
    fun `payment removes exact amount from dissimilar matching stacks and preserves leftovers`() {
        val firstMeta = mock(ItemMeta::class.java)
        val secondMeta = mock(ItemMeta::class.java)
        val contents = arrayOf(
            stack(Material.DIAMOND, 5, firstMeta),
            stack(Material.DIAMOND, 12, secondMeta),
            stack(Material.EMERALD, 20),
            null
        )
        val payment = GuildItemPayment(inventory(contents), set("diamonds" to 8),
            config("diamonds" to GuildItemDefinition("DIAMOND")))

        assertTrue(payment.isSatisfied)
        assertEquals(17, payment.availableCounts["diamonds"])
        payment.removeItems()
        assertNull(contents[0])
        assertEquals(9, contents[1]!!.amount)
        assertSame(secondMeta, contents[1]!!.itemMeta)
        assertEquals(20, contents[2]!!.amount)
    }

    @Test
    fun `name-only payment spans different types`() {
        val meta = mock(ItemMeta::class.java)
        `when`(meta.displayName()).thenReturn(ItemUtils.coloredWithoutItalics("Key"))
        val contents = arrayOf<ItemStack?>(stack(Material.STICK, 3, meta), stack(Material.DIAMOND, 5, meta))
        val payment = GuildItemPayment(inventory(contents), set("key" to 6),
            config("key" to GuildItemDefinition().also { it.name = "Key" }))

        assertTrue(payment.isSatisfied)
        payment.removeItems()
        assertNull(contents[0])
        assertEquals(2, contents[1]!!.amount)
        assertSame(meta, contents[1]!!.itemMeta)
    }

    @Test
    fun `allocation moves broad requirements away from items needed by another requirement`() {
        // Inventory: 10 diamonds named "Key", then 10 ordinary diamonds.
        // Cost: 15 diamonds of any name, plus 5 items named "Key" of any material.
        // Five named diamonds must be reserved for keys; the rest pay the diamond cost.
        val named = mock(ItemMeta::class.java)
        `when`(named.displayName()).thenReturn(ItemUtils.coloredWithoutItalics("Key"))
        val contents = arrayOf<ItemStack?>(stack(Material.DIAMOND, 10, named), stack(Material.DIAMOND, 10))
        val requirements = set("diamonds" to 15, "keys" to 5)
        val definitions = config("diamonds" to GuildItemDefinition("DIAMOND"),
            "keys" to GuildItemDefinition().also { it.name = "Key" })
        val payment = GuildItemPayment(inventory(contents), requirements, definitions)

        assertTrue(payment.isSatisfied)
        assertEquals(mapOf("diamonds" to 15, "keys" to 5), payment.availableCounts)
        payment.removeItems()
        assertTrue(contents.all { it == null })
    }

    @Test
    fun `allocation reassigns several requirements across storage slots with gaps`() {
        // The only complete payment is:
        //   diamond cost <- diamond named "Key"
        //   key cost     <- emerald named "Key"
        //   fee cost     <- diamond with lore ["Fee"]
        // ItemAllocationGraphTest explains why reaching it requires two reassignments.
        val feeMeta = mock(ItemMeta::class.java)
        `when`(feeMeta.lore()).thenReturn(listOf(ItemUtils.coloredWithoutItalics("Fee")))
        val keyMeta = mock(ItemMeta::class.java)
        `when`(keyMeta.displayName()).thenReturn(ItemUtils.coloredWithoutItalics("Key"))
        val unrelated = stack(Material.STONE, 7)
        val contents = arrayOf(null, stack(Material.DIAMOND, 1, feeMeta), null,
            stack(Material.DIAMOND, 1, keyMeta), stack(Material.EMERALD, 1, keyMeta), unrelated)
        val requirements = set("diamond" to 1, "key" to 1, "fee" to 1)
        val definitions = config(
            "diamond" to GuildItemDefinition("DIAMOND"),
            "key" to GuildItemDefinition().also { it.name = "Key" },
            "fee" to GuildItemDefinition().also { it.lore = listOf("Fee") }
        )
        val payment = GuildItemPayment(inventory(contents), requirements, definitions)

        assertTrue(payment.isSatisfied)
        assertEquals(mapOf("diamond" to 1, "key" to 1, "fee" to 1), payment.availableCounts)
        payment.removeItems()
        assertTrue(contents.take(5).all { it == null })
        assertSame(unrelated, contents[5])
        assertEquals(7, contents[5]!!.amount)
    }

    @Test
    fun `one stack cannot pay twice and failed payment removes nothing`() {
        val contents = arrayOf<ItemStack?>(stack(Material.DIAMOND, 10))
        val payment = GuildItemPayment(inventory(contents), set("first" to 6, "second" to 6),
            config("first" to GuildItemDefinition("DIAMOND"), "second" to GuildItemDefinition("DIAMOND")))

        assertFalse(payment.isSatisfied)
        assertEquals(10, payment.availableCounts.values.sum())
        assertFailsWith<IllegalStateException> { payment.removeItems() }
        assertEquals(10, contents[0]!!.amount)
    }

    @Test
    fun `missing library entries fail closed and disabled requirements remove nothing`() {
        val contents = arrayOf<ItemStack?>(stack(Material.DIAMOND, 10))
        val inventory = inventory(contents)
        val requirements = set("unknown" to 1)
        val definitions = config()
        assertFalse(GuildItemPayment(inventory, requirements, definitions).isSatisfied)
        requirements.requirements.itemsEnabled = false
        val disabled = GuildItemPayment(inventory, requirements, definitions)
        assertTrue(disabled.isSatisfied)
        disabled.removeItems()
        assertEquals(10, contents[0]!!.amount)
    }

    @Test
    fun `checker and payment ignore armor offhand and ender chest for satisfying requirements`() {
        val contents = arrayOf<ItemStack?>(stack(Material.DIAMOND, 2))
        val inventory = inventory(contents)
        val excluded = stack(Material.DIAMOND, 64)
        `when`(inventory.contents).thenReturn(arrayOf(contents[0], excluded))
        val ender = mock(Inventory::class.java)
        val enderItem = stack(Material.DIAMOND, 64)
        `when`(ender.storageContents).thenReturn(arrayOf(enderItem))
        val player = mock(Player::class.java)
        `when`(player.inventory).thenReturn(inventory)
        `when`(player.enderChest).thenReturn(ender)

        val result = GuildItemRequirementChecker().check(player, null, set("diamonds" to 3),
            config("diamonds" to GuildItemDefinition("DIAMOND")))
        assertFalse(result.meetsAll())
        assertEquals(2, result.itemCounts["diamonds"]!!.inv)
        assertEquals(64, result.itemCounts["diamonds"]!!.ender)
        assertEquals(1, result.itemCounts["diamonds"]!!.missing)
    }
}
