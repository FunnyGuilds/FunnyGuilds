package net.dzikoysk.funnyguilds.config

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils
import net.kyori.adventure.text.Component
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito.CALLS_REAL_METHODS
import org.mockito.Mockito.mockStatic
import java.nio.file.Path
import kotlin.test.assertEquals
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
    fun `failed reload retains previous item definitions sets GUI and cached resolved values`(@TempDir directory: Path) {
        val file = directory.resolve("items.yml").toFile()
        file.writeText("guild-item-library:\n  key:\n    name: Key")
        val config = create(file)
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
