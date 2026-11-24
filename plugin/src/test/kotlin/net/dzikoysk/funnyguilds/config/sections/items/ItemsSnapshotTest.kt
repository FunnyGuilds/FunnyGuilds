package net.dzikoysk.funnyguilds.config.sections.items

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ItemsSnapshotTest {

    private fun definition(material: String): GuildItemDefinition = GuildItemDefinition(material)

    private fun snapshot(
        library: Map<String, GuildItemDefinition> = mapOf("diamonds" to definition("DIAMOND")),
        toggles: Map<String, GuildItemToggleDefinition> = mapOf(),
        sets: Map<String, GuildItemSet> = mapOf(),
    ): ItemsSnapshot = ItemsSnapshot(library, toggles, sets, "funnyguilds.items.admin.bypass", GuiConfiguration())

    @Test
    fun `snapshot copies maps so later source mutation never leaks in`() {
        val library = LinkedHashMap<String, GuildItemDefinition>()
        library["diamonds"] = definition("DIAMOND")
        val sets = LinkedHashMap<String, GuildItemSet>()
        val items = snapshot(library = library, sets = sets)

        library["staged"] = definition("EMERALD")
        sets["staged"] = GuildItemSet()

        assertEquals(setOf("diamonds"), items.guildItemLibrary.keys)
        assertTrue(items.guildItemSets.isEmpty())
    }

    @Test
    fun `snapshot maps reject modification attempts`() {
        val items = snapshot()

        assertFailsWith<UnsupportedOperationException> { items.guildItemLibrary["staged"] = definition("STONE") }
        assertFailsWith<UnsupportedOperationException> { items.guildItemLibrary.clear() }
        assertFailsWith<UnsupportedOperationException> { items.guildToggleLibrary.clear() }
        assertFailsWith<UnsupportedOperationException> { items.guildItemSets.clear() }
    }

    @Test
    fun `sets are sorted by priority from the highest to the lowest`() {
        val low = GuildItemSet().apply { priority = 1 }
        val high = GuildItemSet().apply { priority = 10 }
        val items = snapshot(sets = linkedMapOf("low" to low, "high" to high))

        assertEquals(listOf(high, low), items.getSetsSortedByPriority())
    }

    @Test
    fun `set lookup matches exact name first and then ignores case`() {
        val set = GuildItemSet()
        val items = snapshot(sets = linkedMapOf("vip" to set))

        assertSame(set, items.findGuildItemSet("vip", true).orElseThrow())
        assertSame(set, items.findGuildItemSet("VIP", true).orElseThrow())
        assertFalse(items.findGuildItemSet("VIP", false).isPresent)
        assertFalse(items.findGuildItemSet("default", true).isPresent)
    }

    @Test
    fun `library lookup falls back to the toggle all state`() {
        val all = definition("LIME_DYE")
        val toggles = linkedMapOf("item-t" to GuildItemToggleDefinition().apply { this.all = all })
        val items = snapshot(toggles = toggles)

        assertSame(all, items.getLibraryItem("item-t").orElseThrow())
        assertSame(toggles.getValue("item-t"), items.getToggleItem("item-t").orElseThrow())
        assertFalse(items.getToggleItem("unknown").isPresent)
    }

    @Test
    fun `per-set overrides win over the library`() {
        val override = definition("PAPER")
        val perSet = PerSetConfig().apply { libraryOverrides = linkedMapOf("diamonds" to override) }
        val items = snapshot()

        assertSame(override, items.resolveItem("diamonds", perSet).orElseThrow())
        assertNotSame(override, items.resolveItem("diamonds", null).orElseThrow())
        assertEquals("DIAMOND", items.resolveItem("diamonds", null).orElseThrow().material)
    }

}
