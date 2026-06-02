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
 * Verifies that the typed [ExplosionControlConfiguration] survives an okaeri save/load round-trip.
 */
class ExplosionControlRoundTripTest {

    class SampleConfig : OkaeriConfig() {
        @JvmField
        @CustomKey("explosion-control")
        var explosionControl = ExplosionControlConfiguration()
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
        loaded.explosionControl.loadProcessedProperties()

        assertEquals(3, loaded.explosionControl.guild.radius)
        assertEquals(3, loaded.explosionControl.global.radius)
        assertFalse(loaded.explosionControl.guild.dropsVanillaBlocks())
        assertEquals(20.0, loaded.explosionControl.guild.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertEquals(33.0, loaded.explosionControl.global.explosionChance(Material.WATER)!!, 1e-9)
    }

    @Test
    fun `custom configuration round-trips through okaeri`(@TempDir tempDir: Path) {
        val file = tempDir.resolve("config.yml").toFile()

        create(file).also { cfg ->
            cfg.explosionControl.guild = ExplosionControlScope.of(5, 0.0, linkedMapOf("water" to 33.0, "lava" to 33.0))
            cfg.explosionControl.global = ExplosionControlScope.of(0, -1.0, linkedMapOf("obsidian" to 20.0))
            cfg.save()
        }

        val loaded = create(file)
        loaded.explosionControl.loadProcessedProperties()

        assertEquals(5, loaded.explosionControl.guild.radius)
        assertTrue(loaded.explosionControl.guild.dropsVanillaBlocks())
        assertEquals(33.0, loaded.explosionControl.guild.explosionChance(Material.WATER)!!, 1e-9)
        assertNull(loaded.explosionControl.guild.explosionChance(Material.STONE))

        assertEquals(0, loaded.explosionControl.global.radius)
        assertFalse(loaded.explosionControl.global.dropsVanillaBlocks())
        assertEquals(20.0, loaded.explosionControl.global.explosionChance(Material.OBSIDIAN)!!, 1e-9)
        assertNull(loaded.explosionControl.global.explosionChance(Material.WATER))
    }
}
