package net.dzikoysk.funnyguilds.config

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GuildItemDefinitionTest {

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
