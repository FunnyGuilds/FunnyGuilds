package net.dzikoysk.funnyguilds.config.sections.upgrades

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.CustomKey
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

/**
 * Verifies that the typed [GuildUpgradesConfiguration] survives an okaeri save/load round-trip.
 */
class GuildUpgradesRoundTripTest {

    class SampleConfig : OkaeriConfig() {
        @JvmField
        @CustomKey("guild-upgrades")
        var guildUpgrades = GuildUpgradesConfiguration()
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

        assertTrue(loaded.guildUpgrades.upgrades.containsKey("points_boost"))
        assertTrue(loaded.guildUpgrades.upgrades.containsKey("members"))
        assertEquals(GuildUpgradesConfiguration.EconomyType.VAULT, loaded.guildUpgrades.economyType)

        val pointsBoost = loaded.guildUpgrades.findUpgrade("points_boost").orElseThrow()
        assertEquals(3, pointsBoost.maxLevel)
        assertEquals(5.0, pointsBoost.getLevel(1).orElseThrow().value)
        assertEquals(15.0, pointsBoost.getLevel(3).orElseThrow().value)
    }

    @Test
    fun `custom configuration round-trips through okaeri`(@TempDir tempDir: Path) {
        val file = tempDir.resolve("config.yml").toFile()

        create(file).also { cfg ->
            cfg.guildUpgrades.enabled = true
            cfg.guildUpgrades.economyType = GuildUpgradesConfiguration.EconomyType.ITEM
            cfg.guildUpgrades.upgrades["members"]!!.levels.add(UpgradeLevelDefinition(8.0, 60000.0))
            cfg.save()
        }

        val loaded = create(file)

        assertTrue(loaded.guildUpgrades.enabled)
        assertEquals(GuildUpgradesConfiguration.EconomyType.ITEM, loaded.guildUpgrades.economyType)

        val members = loaded.guildUpgrades.findUpgrade("MEMBERS").orElseThrow()
        assertEquals(4, members.maxLevel)
        assertEquals(8.0, members.getLevel(4).orElseThrow().value)
    }
}
