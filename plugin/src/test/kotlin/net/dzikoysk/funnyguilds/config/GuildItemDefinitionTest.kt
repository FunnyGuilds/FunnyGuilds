package net.dzikoysk.funnyguilds.config

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.FunnyGuildsLogger
import net.dzikoysk.funnyguilds.config.message.MessageService
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration
import net.dzikoysk.funnyguilds.config.sections.items.ItemsSnapshot
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration
import net.dzikoysk.funnyguilds.feature.hooks.HookManager
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils
import net.kyori.adventure.text.Component
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito.CALLS_REAL_METHODS
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import java.nio.file.Path
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class GuildItemDefinitionTest {

    private fun create(file: java.io.File): ItemsConfiguration =
        ConfigManager.create(ItemsConfiguration::class.java) { cfg ->
            cfg.configure { options ->
                options.configurer(YamlBukkitConfigurer(), SerdesCommons())
                options.bindFile(file)
            }
            cfg.load(true)
        }

    @Test
    fun `invalid values in item toggle and GUI definitions fail during loading with their full paths`(@TempDir directory: Path) {
        val examples = listOf(
            "guild-item-library:\n  key:\n    material: TYPO" to "guild-item-library.key.material",
            "guild-item-library:\n  key:\n    flags: [TYPO]" to "guild-item-library.key.flags",
            "guild-toggle-library:\n  toggle:\n    missing-only:\n      armor-color: '256_0_0'" to
                "guild-toggle-library.toggle.missing-only.armor-color",
            "gui:\n  per-set:\n    vip:\n      library-overrides:\n        key:\n          armor-color: 'red_green_blue'" to
                "gui.per-set.vip.library-overrides.key.armor-color",
            "guild-item-library:\n  key:\n    armor-color: '1_2'" to "guild-item-library.key.armor-color"
        )
        for ((yaml, path) in examples) {
            val file = directory.resolve("items.yml").toFile()
            file.writeText(yaml)
            val error = assertFailsWith<RuntimeException> { create(file) }
            val validation = generateSequence<Throwable>(error) { it.cause }
                .filterIsInstance<IllegalArgumentException>().first()
            assertTrue(validation.message!!.contains("items.yml.$path"), validation.message)
        }
    }

    @Test
    fun `candidate definitions stay invisible to readers until all validation succeeds`(@TempDir directory: Path) {
        val file = directory.resolve("items.yml").toFile()
        file.writeText("guild-item-library:\n  key:\n    name: Original")
        val config = create(file)
        val activeLibrary = config.guildItemLibrary
        val activeTitle = config.gui.title
        mockStatic(ItemUtils::class.java, CALLS_REAL_METHODS).use { itemUtils ->
            itemUtils.`when`<Component> { ItemUtils.coloredWithoutItalics("Staged") }.thenAnswer { invocation ->
                assertSame(activeLibrary, config.guildItemLibrary)
                assertEquals(activeTitle, config.gui.title)
                invocation.callRealMethod()
            }
            assertFailsWith<IllegalArgumentException> {
                config.load("""
                    guild-item-library:
                      staged:
                        name: Staged
                      broken:
                        material: TYPO
                    gui:
                      title: Staged title
                """.trimIndent())
            }
        }
        assertSame(activeLibrary, config.guildItemLibrary)
        assertEquals("Original", config.guildItemLibrary["key"]!!.name)
    }

    @Test
    fun `operations keep their sets library and GUI from the same load across reload`(@TempDir directory: Path) {
        val file = directory.resolve("items.yml").toFile()
        fun writeVersion(key: String, material: String) {
            file.writeText("""
                guild-item-library:
                  $key: {material: $material}
                guild-item-sets:
                  default:
                    items: {$key: 1}
                gui:
                  title: $key
                  per-set:
                    default:
                      library-overrides:
                        $key: {material: PAPER}
            """.trimIndent())
        }

        writeVersion("old", "DIAMOND")
        val config = create(file)
        val captured = config.snapshot()
        val selectedSet = captured.guildItemSets.getValue("default")

        writeVersion("new", "EMERALD")
        config.load()

        val oldKey = selectedSet.items.keys.single()
        assertEquals("old", oldKey)
        assertEquals("DIAMOND", captured.getLibraryItem(oldKey).orElseThrow().material)
        assertEquals("old", captured.gui.title)
        assertEquals("PAPER", captured.resolveItem(oldKey, captured.gui.getPerSetConfig("default")).orElseThrow().material)

        val latest = config.snapshot()
        assertEquals(setOf("new"), latest.guildItemSets.getValue("default").items.keys)
        assertEquals("EMERALD", latest.getLibraryItem("new").orElseThrow().material)
        assertEquals("new", latest.gui.title)
    }

    @Test
    fun `published snapshot does not expose mutable configuration settings`(@TempDir directory: Path) {
        val file = directory.resolve("items.yml").toFile()
        file.writeText("guild-item-library:\n  key: {material: DIAMOND}")
        val config = create(file)
        val snapshot = config.snapshot()
        assertIs<ItemsSnapshot>(snapshot)

        val library = snapshot.guildItemLibrary
        assertFailsWith<UnsupportedOperationException> { library["staged"] = ItemsSnapshot.ItemDefinition(GuildItemDefinition("STONE")) }
        assertFailsWith<UnsupportedOperationException> { library.clear() }
        assertFailsWith<UnsupportedOperationException> { snapshot.guildToggleLibrary.clear() }
        assertFailsWith<UnsupportedOperationException> { snapshot.guildItemSets.clear() }

        // Mutating the live configuration afterwards must not leak into a previously captured snapshot.
        val capturedBefore = config.snapshot()
        config.guildItemLibrary["staged"] = GuildItemDefinition("STONE")
        assertEquals(setOf("key"), capturedBefore.guildItemLibrary.keys)
        assertSame(capturedBefore, config.snapshot())
    }

    @Test
    fun `failed reload retains previous item definitions sets GUI and cached resolved values`(@TempDir directory: Path) {
        val file = directory.resolve("items.yml").toFile()
        file.writeText("guild-item-library:\n  key:\n    name: Key")
        val config = create(file)
        val snapshot = config.snapshot()
        val definition = config.guildItemLibrary["key"]!!
        val resolved = definition.resolved
        val oldGuiTitle = config.gui.title
        val oldCost = config.guildItemSets["default"]!!.items
        file.writeText("""
            guild-item-library:
              key:
                material: TYPO
            guild-item-sets:
              default:
                items: {key: 99}
            gui:
              title: Changed
        """.trimIndent())

        assertFailsWith<IllegalArgumentException> { config.load() }
        assertSame(snapshot, config.snapshot())
        assertSame(definition, config.guildItemLibrary["key"])
        assertSame(resolved, config.guildItemLibrary["key"]!!.resolved)
        assertEquals(oldCost, config.guildItemSets["default"]!!.items)
        assertEquals(oldGuiTitle, config.gui.title)

        file.writeText("guild-item-library:\n  key:\n    name: Updated")
        config.load()
        assertEquals("Updated", config.guildItemLibrary["key"]!!.name)
        assertEquals("Updated", (config.guildItemLibrary["key"]!!.resolved.name() as net.kyori.adventure.text.TextComponent).content())
    }

    @Test
    fun `failed snapshot construction leaves active configuration and snapshot untouched`(@TempDir directory: Path) {
        val file = directory.resolve("items.yml").toFile()
        file.writeText("guild-item-library:\n  key:\n    name: Original")
        val probe = object : ItemsConfiguration() {
            var failSnapshotConstruction = false
            override fun constructSnapshot(candidate: ItemsConfiguration): ItemsSnapshot {
                if (failSnapshotConstruction) {
                    throw IllegalStateException("snapshot construction failed")
                }
                return super.constructSnapshot(candidate)
            }
        }
        val config = ConfigManager.initialize(probe)
        config.configure { options ->
            options.configurer(YamlBukkitConfigurer(), SerdesCommons())
            options.bindFile(file)
        }
        config.load(true)
        val previousSnapshot = config.snapshot()
        val previousLibrary = config.guildItemLibrary

        probe.failSnapshotConstruction = true
        assertFailsWith<IllegalStateException> { config.load() }
        probe.failSnapshotConstruction = false

        assertSame(previousSnapshot, config.snapshot())
        assertSame(previousLibrary, config.guildItemLibrary)
        assertEquals("Original", config.guildItemLibrary["key"]!!.name)
        assertFalse(config.guildItemLibrary.containsKey("staged"))
    }

    @Test
    fun `plugin reload reports the rejected item path and continues reloading services`(@TempDir directory: Path) {
        val file = directory.resolve("items.yml").toFile()
        file.writeText("guild-item-library:\n  key: {material: DIAMOND}")
        val items = create(file)
        val active = items.snapshot()
        file.writeText("guild-item-library:\n  key: {material: TYPO}")

        val plugin = mock(FunnyGuilds::class.java, CALLS_REAL_METHODS)
        val config = mock(PluginConfiguration::class.java)
        config.scoreboard = net.dzikoysk.funnyguilds.config.sections.ScoreboardConfiguration().apply { enabled = false }
        val tablist = mock(TablistConfiguration::class.java)
        val messages = mock(MessageService::class.java)
        val hooks = mock(HookManager::class.java)
        for ((field, value) in mapOf(
            "pluginConfiguration" to config,
            "itemsConfiguration" to items,
            "tablistConfiguration" to tablist,
            "messageService" to messages,
            "hookManager" to hooks,
            "nameTagUpdateTask" to Optional.empty<Any>(),
            "dummyUpdateTask" to Optional.empty<Any>(),
            "scoreboardQueueUpdateTask" to Optional.empty<Any>()
        )) {
            FunnyGuilds::class.java.getDeclaredField(field).apply { isAccessible = true }.set(plugin, value)
        }
        val reported = mutableListOf<String>()
        val logger = mock(FunnyGuildsLogger::class.java)
        mockStatic(FunnyGuilds::class.java).use {
            it.`when`<FunnyGuildsLogger> { FunnyGuilds.getPluginLogger() }.thenReturn(logger)
            plugin.reloadConfiguration { error -> reported.add(error) }
        }

        assertTrue(reported.single().contains("items.yml.guild-item-library.key.material"))
        assertSame(active, items.snapshot())
        verify(config).load()
        verify(tablist).load()
        verify(messages).reload()
        verify(hooks).callConfigUpdated()
    }

    @Test
    fun `name-only and empty definitions remain optional through YAML save load and reload`(@TempDir directory: Path) {
        val file = directory.resolve("items.yml").toFile()
        file.writeText("""
            guild-item-library:
              key:
                name: '<gold>Key'
              any-item: {}
              diamond:
                material: DIAMOND
        """.trimIndent())
        val config = ConfigManager.create(ItemsConfiguration::class.java) { cfg ->
            cfg.configure { options ->
                options.configurer(YamlBukkitConfigurer(), SerdesCommons())
                options.bindFile(file)
            }
            cfg.load(true)
        }
        repeat(2) {
            val key = config.guildItemLibrary["key"]!!
            assertEquals("<gold>Key", key.name)
            assertNull(key.material)
            assertNull(key.lore)
            assertNull(key.enchants)
            assertNull(key.flags)
            assertNull(key.customModelData)
            assertNull(key.skullOwner)
            assertNull(key.armorColor)
            assertNull(config.guildItemLibrary["any-item"]!!.material)
            assertEquals("DIAMOND", config.guildItemLibrary["diamond"]!!.material)
            assertNull(config.guildItemLibrary["diamond"]!!.name)
            config.save()
            config.load()
        }
    }
}
