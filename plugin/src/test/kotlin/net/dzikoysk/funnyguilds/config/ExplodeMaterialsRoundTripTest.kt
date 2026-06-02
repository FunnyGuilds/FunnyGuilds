package net.dzikoysk.funnyguilds.config

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.CustomKey
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
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
 * Verifies that the typed [ExplodeMaterialsConfiguration] survives an okaeri save/load round-trip.
 */
class ExplodeMaterialsRoundTripTest {

    class SampleConfig : OkaeriConfig() {
        @JvmField
        @CustomKey("explode-materials")
        var explodeMaterials = ExplodeMaterialsConfiguration()
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

    @Test
    fun `default configuration round-trips through okaeri`(@TempDir tempDir: Path) {
        val file = tempDir.resolve("config.yml").toFile()

        create(file)
        val loaded = create(file)
        loaded.explodeMaterials.loadProcessedProperties()

        assertFalse(loaded.explodeMaterials.guild.dropsVanillaBlocks())
        assertFalse(loaded.explodeMaterials.global.dropsVanillaBlocks())
        assertEquals(20.0, loaded.explodeMaterials.guild.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertEquals(33.0, loaded.explodeMaterials.global.explosionChance(Material.WATER)!!, 1e-9)
    }

    @Test
    fun `custom configuration round-trips through okaeri`(@TempDir tempDir: Path) {
        val file = tempDir.resolve("config.yml").toFile()

        create(file).also { cfg ->
            cfg.explodeMaterials.guild = ExplodeMaterialsScope.of(0.0, linkedMapOf("water" to 33.0, "lava" to 33.0))
            cfg.explodeMaterials.global = ExplodeMaterialsScope.of(-1.0, linkedMapOf("obsidian" to 20.0))
            cfg.save()
        }

        val loaded = create(file)
        loaded.explodeMaterials.loadProcessedProperties()

        assertTrue(loaded.explodeMaterials.guild.dropsVanillaBlocks())
        assertEquals(33.0, loaded.explodeMaterials.guild.explosionChance(Material.WATER)!!, 1e-9)
        assertNull(loaded.explodeMaterials.guild.explosionChance(Material.STONE))

        assertFalse(loaded.explodeMaterials.global.dropsVanillaBlocks())
        assertEquals(20.0, loaded.explodeMaterials.global.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertNull(loaded.explodeMaterials.global.explosionChance(Material.WATER))
    }
}
