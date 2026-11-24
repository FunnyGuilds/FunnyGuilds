package net.dzikoysk.funnyguilds.config.sections.items

import org.junit.jupiter.api.Test
import java.util.LinkedHashMap
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ItemsSnapshotTest {

    private fun definition(material: String): GuildItemDefinition = GuildItemDefinition(material)

    private fun configuration(
        library: Map<String, GuildItemDefinition> = mapOf("diamonds" to definition("DIAMOND")),
        toggles: Map<String, GuildItemToggleDefinition> = mapOf(),
        sets: Map<String, GuildItemSet> = mapOf(),
    ): ItemsConfiguration = ItemsConfiguration().also {
        it.guildItemLibrary = LinkedHashMap(library)
        it.guildToggleLibrary = LinkedHashMap(toggles)
        it.guildItemSets = LinkedHashMap(sets)
    }

    private fun snapshot(
        library: Map<String, GuildItemDefinition> = mapOf("diamonds" to definition("DIAMOND")),
        toggles: Map<String, GuildItemToggleDefinition> = mapOf(),
        sets: Map<String, GuildItemSet> = mapOf(),
    ): ItemsSnapshot = ItemsSnapshot(
        LinkedHashMap(library),
        LinkedHashMap(toggles),
        LinkedHashMap(sets),
        "funnyguilds.items.admin.bypass",
        GuiConfiguration()
    )

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
    fun `snapshot values are detached from the source objects`() {
        val set = GuildItemSet().apply {
            priority = 5
            items = linkedMapOf("diamonds" to 64)
        }
        val gui = GuiConfiguration().apply { title = "Original" }
        val items = ItemsSnapshot(
            linkedMapOf("diamonds" to definition("DIAMOND")),
            linkedMapOf(),
            linkedMapOf("default" to set),
            "funnyguilds.items.admin.bypass",
            gui
        )

        set.priority = 99
        set.items.clear()
        gui.title = "Changed"
        gui.pattern.clear()

        assertEquals(5, items.guildItemSets.getValue("default").priority)
        assertEquals(mapOf("diamonds" to 64), items.guildItemSets.getValue("default").items)
        assertEquals("Original", items.gui.title)
        assertEquals(6, items.gui.pattern.size)
    }

    @Test
    fun `nested snapshot values reject mutation attempts`() {
        val items = ItemsSnapshot(
            linkedMapOf("diamonds" to definition("DIAMOND").apply {
                lore = arrayListOf("line")
            }),
            linkedMapOf(),
            linkedMapOf("default" to GuildItemSet().apply { items = linkedMapOf("diamonds" to 64) }),
            "funnyguilds.items.admin.bypass",
            GuiConfiguration()
        )

        val item = items.guildItemLibrary.getValue("diamonds")
        assertFailsWith<UnsupportedOperationException> { item.lore!!.add("staged") }
        assertFailsWith<UnsupportedOperationException> { items.guildItemSets.getValue("default").items.clear() }
        assertFailsWith<UnsupportedOperationException> { items.gui.pattern.add("staged") }
        assertFailsWith<UnsupportedOperationException> { items.gui.itemDisplay.hasEnough.additionalLore!!.clear() }
        assertFailsWith<UnsupportedOperationException> { items.gui.perSet["staged"] = ItemsSnapshot.PerSet(null, mapOf()) }
    }

    @Test
    fun `per-set overrides are detached from the source configuration`() {
        val perSet = PerSetConfig().apply {
            title = "Original"
            libraryOverrides = linkedMapOf("diamonds" to definition("PAPER"))
        }
        val gui = GuiConfiguration().apply { this.perSet = linkedMapOf("default" to perSet) }
        val items = ItemsSnapshot(linkedMapOf(), linkedMapOf(), linkedMapOf(), "bypass", gui)

        perSet.title = "Changed"
        perSet.libraryOverrides.clear()

        val captured = items.gui.getPerSetConfig("default")
        assertEquals("Original", captured.title)
        assertEquals("PAPER", captured.libraryOverrides.getValue("diamonds").material)
    }

    @Test
    fun `per-set constructor rejects mutation including through the shared empty value`() {
        val perSet = ItemsSnapshot.PerSet("Title", linkedMapOf("diamonds" to ItemsSnapshot.ItemDefinition(definition("PAPER"))))
        assertFailsWith<UnsupportedOperationException> { perSet.libraryOverrides.clear() }

        val empty = ItemsSnapshot.of(configuration()).gui.getPerSetConfig("nonexistent")
        assertFailsWith<UnsupportedOperationException> { empty.libraryOverrides["extra"] = ItemsSnapshot.ItemDefinition(definition("PAPER")) }
    }

    @Test
    fun `two snapshots of the same configuration are independent`() {
        val config = configuration()
        val first = ItemsSnapshot.of(config)

        config.guildItemLibrary["diamonds"]!!.name = "Changed"
        config.guildItemSets["default"] = GuildItemSet()

        val second = ItemsSnapshot.of(config)
        assertEquals(null, first.getLibraryItem("diamonds").orElseThrow().name)
        assertEquals("Changed", second.getLibraryItem("diamonds").orElseThrow().name)
        assertTrue(first.guildItemSets.isEmpty())
        assertEquals(setOf("default"), second.guildItemSets.keys)
    }

    @Test
    fun `sets are sorted by priority from the highest to the lowest`() {
        val low = GuildItemSet().apply { priority = 1 }
        val high = GuildItemSet().apply { priority = 10 }
        val items = snapshot(sets = linkedMapOf("low" to low, "high" to high))

        assertEquals(listOf(10, 1), items.getSetsSortedByPriority().map { it.priority })
    }

    @Test
    fun `snapshot preserves the configured insertion order`() {
        val library = LinkedHashMap<String, GuildItemDefinition>()
        library["zebra"] = definition("DIAMOND")
        library["alpha"] = definition("EMERALD")
        library["middle"] = definition("IRON_INGOT")
        val sets = LinkedHashMap<String, GuildItemSet>()
        sets["low"] = GuildItemSet().apply { priority = 1 }
        sets["high"] = GuildItemSet().apply { priority = 1 }
        sets["equal"] = GuildItemSet().apply {
            items = linkedMapOf("zulu" to 1, "alpha" to 2)
        }
        val gui = GuiConfiguration().apply {
            perSet = linkedMapOf("zulu" to PerSetConfig(), "alpha" to PerSetConfig())
        }
        val items = ItemsSnapshot(library, LinkedHashMap(), sets, "bypass", gui)

        assertEquals(listOf("zebra", "alpha", "middle"), items.guildItemLibrary.keys.toList())
        assertEquals(listOf("low", "high", "equal"), items.guildItemSets.keys.toList())
        assertEquals(listOf("zulu", "alpha"), items.guildItemSets.getValue("equal").items.keys.toList())
        assertEquals(listOf("zulu", "alpha"), items.gui.perSet.keys.toList())
    }

    @Test
    fun `snapshot tolerates null lore lines like validation does`() {
        val items = ItemsSnapshot(
            linkedMapOf("diamonds" to definition("DIAMOND").apply {
                lore = arrayListOf(null, "Fee", null)
            }),
            linkedMapOf(),
            linkedMapOf(),
            "bypass",
            GuiConfiguration()
        )

        assertEquals(listOf(null, "Fee", null), items.guildItemLibrary.getValue("diamonds").lore)
        assertFailsWith<UnsupportedOperationException> { items.guildItemLibrary.getValue("diamonds").lore!!.add("staged") }
    }

    @Test
    fun `set lookup matches exact name first and then ignores case`() {
        val set = GuildItemSet().apply { permission = "funnyguilds.items.vip" }
        val items = snapshot(sets = linkedMapOf("vip" to set))

        assertEquals("funnyguilds.items.vip", items.findGuildItemSet("vip", true).orElseThrow().permission)
        assertEquals("funnyguilds.items.vip", items.findGuildItemSet("VIP", true).orElseThrow().permission)
        assertFalse(items.findGuildItemSet("VIP", false).isPresent)
        assertFalse(items.findGuildItemSet("default", true).isPresent)
    }

    @Test
    fun `library lookup falls back to the toggle all state`() {
        val all = definition("LIME_DYE")
        val toggles = linkedMapOf("item-t" to GuildItemToggleDefinition().apply { this.all = all })
        val items = snapshot(toggles = toggles)

        assertEquals("LIME_DYE", items.getLibraryItem("item-t").orElseThrow().material)
        assertEquals("LIME_DYE", items.getToggleItem("item-t").orElseThrow().all!!.material)
        assertFalse(items.getToggleItem("unknown").isPresent)
    }

    @Test
    fun `per-set overrides win over the library`() {
        val perSetConfig = PerSetConfig().apply {
            libraryOverrides = linkedMapOf("diamonds" to definition("PAPER"))
        }
        val perSet = ItemsSnapshot.PerSet(
            perSetConfig.title,
            perSetConfig.libraryOverrides.mapValues { (_, value) -> ItemsSnapshot.ItemDefinition(value) }
        )
        val items = snapshot()

        assertEquals("PAPER", items.resolveItem("diamonds", perSet).orElseThrow().material)
        assertEquals("DIAMOND", items.resolveItem("diamonds", null).orElseThrow().material)
    }

}
