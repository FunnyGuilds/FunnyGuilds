package net.dzikoysk.funnyguilds.config

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.CustomKey
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import net.dzikoysk.funnyguilds.config.ExplodeMaterialsScope.WildcardMode
import org.bukkit.Material
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

/**
 * Verifies that the `Map<String, Object>` representation of `explode-materials` survives an okaeri save/load
 * round-trip (this is the shape used by [PluginConfiguration]), regardless of whether nested sections come back
 * as a [Map] or a Bukkit `ConfigurationSection`.
 */
class ExplodeMaterialsRoundTripTest {

    class SampleConfig : OkaeriConfig() {
        @CustomKey("explode-materials")
        @JvmField
        var explodeMaterials: MutableMap<String, Any> = ExplodeMaterialsScope.defaultConfiguration()
    }

    private fun create(file: File): SampleConfig =
        ConfigManager.create(SampleConfig::class.java) { cfg ->
            cfg.configure { opt ->
                opt.configurer(YamlBukkitConfigurer(), SerdesCommons())
                opt.bindFile(file)
            }
            cfg.saveDefaults()
            cfg.load(true)
        }

    @Suppress("UNCHECKED_CAST")
    private fun scope(config: SampleConfig, key: String): ExplodeMaterialsScope {
        val value = config.explodeMaterials[key]
        val raw = when (value) {
            is Map<*, *> -> value as Map<String, Any>
            is org.bukkit.configuration.ConfigurationSection -> value.getValues(false)
            else -> emptyMap()
        }
        return ExplodeMaterialsScope.parse(raw)
    }

    @Test
    fun `nested default configuration round-trips through okaeri`(@TempDir tempDir: Path) {
        val file = tempDir.resolve("config.yml").toFile()

        create(file)              // writes the nested default
        val loaded = create(file) // reads it back

        val guild = scope(loaded, "guild")
        val global = scope(loaded, "global")

        assertEquals(WildcardMode.DEFAULT, guild.wildcardMode)
        assertEquals(WildcardMode.DEFAULT, global.wildcardMode)
        assertEquals(20.0, guild.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertEquals(33.0, global.explosionChance(Material.WATER)!!, 1e-9)
    }

    @Test
    fun `custom scoped configuration round-trips through okaeri`(@TempDir tempDir: Path) {
        val file = tempDir.resolve("config.yml").toFile()

        // Write a hand-authored config mirroring the issue's requested layout.
        create(file).also { cfg ->
            cfg.explodeMaterials = linkedMapOf(
                "guild" to linkedMapOf<String, Any>("*" to "none", "water" to 33.0, "lava" to 33.0),
                "global" to linkedMapOf<String, Any>("*" to "default", "obsidian" to 20.0)
            )
            cfg.save()
        }

        val loaded = create(file)
        val guild = scope(loaded, "guild")
        val global = scope(loaded, "global")

        assertEquals(WildcardMode.NONE, guild.wildcardMode)
        assertEquals(33.0, guild.explosionChance(Material.WATER)!!, 1e-9)
        assertNull(guild.explosionChance(Material.STONE))

        assertEquals(WildcardMode.DEFAULT, global.wildcardMode)
        assertEquals(20.0, global.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertNull(global.explosionChance(Material.WATER))
    }
}
