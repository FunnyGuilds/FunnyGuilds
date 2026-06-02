package net.dzikoysk.funnyguilds.config

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.CustomKey
import eu.okaeri.configs.migrate.view.RawConfigView
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import net.dzikoysk.funnyguilds.config.ExplodeMaterialsScope.WildcardMode
import org.bukkit.Material
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

/**
 * Validates the okaeri mechanism used by [PluginConfiguration] to migrate the legacy flat `explode-materials`
 * map (plus the removed `explode-should-affect-only-guild` flag) into the new `guild`/`global` structure:
 * reading an undeclared legacy key via [RawConfigView] after binding, rewriting the field, and dropping the
 * orphan key on save. The migration body mirrors `PluginConfiguration#migrateLegacyExplodeMaterials`.
 */
class ExplodeMaterialsMigrationTest {

    class MigratingConfig : OkaeriConfig() {
        @CustomKey("explode-materials")
        @JvmField
        var explodeMaterials: MutableMap<String, Any> = ExplodeMaterialsScope.defaultConfiguration()

        override fun load(): OkaeriConfig {
            super.load()

            val raw = explodeMaterials
            if (!raw.containsKey(ExplodeMaterialsScope.GUILD_SCOPE) && !raw.containsKey(ExplodeMaterialsScope.GLOBAL_SCOPE)) {
                val view = RawConfigView(this)
                val legacyFlag = view.getRaw("explode-should-affect-only-guild")
                val affectOnlyGuild = legacyFlag is Boolean && legacyFlag

                explodeMaterials = ExplodeMaterialsScope.convertLegacy(raw, affectOnlyGuild)
                view.remove("explode-should-affect-only-guild")
            }

            return this
        }
    }

    private fun load(file: File): MigratingConfig =
        ConfigManager.create(MigratingConfig::class.java) { cfg ->
            cfg.configure { opt ->
                opt.configurer(YamlBukkitConfigurer(), SerdesCommons())
                opt.bindFile(file)
            }
            cfg.saveDefaults()
            cfg.load(true)
        }

    @Suppress("UNCHECKED_CAST")
    private fun scope(config: MigratingConfig, key: String): ExplodeMaterialsScope {
        val value = config.explodeMaterials[key]
        val raw = when (value) {
            is Map<*, *> -> value as Map<String, Any>
            is org.bukkit.configuration.ConfigurationSection -> value.getValues(false)
            else -> emptyMap()
        }
        return ExplodeMaterialsScope.parse(raw)
    }

    @Test
    fun `legacy flag enabled migrates to guild materials and global none`(@TempDir tempDir: Path) {
        val file = tempDir.resolve("config.yml").toFile()
        file.writeText(
            """
            explode-materials:
              ender_chest: 20.0
              water: 33.0
            explode-should-affect-only-guild: true
            """.trimIndent()
        )

        val migrated = load(file)

        val guild = scope(migrated, "guild")
        val global = scope(migrated, "global")

        assertEquals(WildcardMode.DEFAULT, guild.wildcardMode)
        assertEquals(20.0, guild.explosionChance(Material.ENDER_CHEST)!!, 1e-9)
        assertEquals(33.0, guild.explosionChance(Material.WATER)!!, 1e-9)

        assertTrue(global.dropsVanillaBlocks())
        assertEquals(WildcardMode.NONE, global.wildcardMode)

        // The legacy flag must be dropped from the persisted file.
        assertFalse(file.readText().contains("explode-should-affect-only-guild"))
        assertTrue(file.readText().contains("guild"))
        assertTrue(file.readText().contains("global"))
    }

    @Test
    fun `legacy flag disabled migrates materials to both scopes`(@TempDir tempDir: Path) {
        val file = tempDir.resolve("config.yml").toFile()
        file.writeText(
            """
            explode-materials:
              obsidian: 20.0
              water: 33.0
            explode-should-affect-only-guild: false
            """.trimIndent()
        )

        val migrated = load(file)

        val guild = scope(migrated, "guild")
        val global = scope(migrated, "global")

        assertEquals(WildcardMode.DEFAULT, guild.wildcardMode)
        assertEquals(WildcardMode.DEFAULT, global.wildcardMode)
        assertEquals(20.0, guild.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertEquals(33.0, global.explosionChance(Material.WATER)!!, 1e-9)
        assertNull(global.explosionChance(Material.STONE))
    }

    @Test
    fun `already migrated configuration is left untouched`(@TempDir tempDir: Path) {
        val file = tempDir.resolve("config.yml").toFile()
        file.writeText(
            """
            explode-materials:
              guild:
                '*': none
                water: 33.0
              global:
                '*': default
                obsidian: 20.0
            """.trimIndent()
        )

        val loaded = load(file)

        val guild = scope(loaded, "guild")
        val global = scope(loaded, "global")

        assertEquals(WildcardMode.NONE, guild.wildcardMode)
        assertEquals(33.0, guild.explosionChance(Material.WATER)!!, 1e-9)
        assertEquals(WildcardMode.DEFAULT, global.wildcardMode)
        assertEquals(20.0, global.explosionChance(Material.OBSIDIAN)!!, 1e-9)
    }
}
