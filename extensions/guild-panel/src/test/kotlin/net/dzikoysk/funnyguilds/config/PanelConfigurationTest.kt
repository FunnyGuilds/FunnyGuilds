package net.dzikoysk.funnyguilds.config

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration
import net.dzikoysk.funnyguilds.feature.items.gui.GuiPattern
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class PanelConfigurationTest {

    @Test
    fun `panel defaults survive an Okaeri round trip`(@TempDir(cleanup = CleanupMode.NEVER) directory: Path) {
        val file = directory.resolve("panel.yml").toFile()
        val configuration = load(file)

        configuration.validateLayout()
        val screen = configuration.screens.overview
        assertEquals(5, screen.rows)
        assertEquals(listOf("fill", "base", "empty", "allies", "empty", "territory", "empty", "validity", "fill"),
            GuiPattern.slots(screen.pattern[3]))
        assertEquals("ENDER_PEARL", screen.items.getValue("base").material)
        assertEquals(27, configuration.screens.relations.pattern.flatMap(GuiPattern::slots).count { it == "entry" })
        assertEquals("SHEARS", configuration.screens.relation.items.getValue("break").material)
        assertEquals(6, configuration.screens.expansion.rows)
        assertEquals(6, configuration.screens.renewal.rows)
        assertEquals(7, configuration.screens.renewal.pattern.flatMap(GuiPattern::slots).count { it == "entry" })
        assertEquals("GREEN_CONCRETE", configuration.screens.expansionMaterials.getValue("current"))
        assertEquals("YELLOW_CONCRETE", configuration.screens.expansionMaterials.getValue("available"))
        assertEquals("RED_CONCRETE", configuration.screens.expansionMaterials.getValue("locked"))
        assertEquals("PLAYER_HEAD", configuration.screens.roles.items.getValue("member").material)
        assertEquals("Teleport do bazy", configuration.screens.permissionNames.getValue("command.base"))
        assertEquals("LAVA_BUCKET", configuration.screens.permissionMaterials.getValue("protection.bucket-empty"))
        assertEquals("PLAYER_HEAD", configuration.screens.roles.items.getValue("invite-target").material)
        assertEquals(36, configuration.screens.audit.pattern.flatMap(GuiPattern::slots).count { it == "entry" })
        assertEquals("HOPPER", configuration.screens.audit.items.getValue("filter").material)
    }

    @Test
    fun `an overview-only config receives the relation screens`(@TempDir(cleanup = CleanupMode.NEVER) directory: Path) {
        val file = directory.resolve("panel.yml").toFile()
        file.writeText("""
            enabled: true
            screens:
              overview:
                title: 'Existing panel'
                pattern:
                - '{base} {set-base}'
                items:
                  base:
                    material: ENDER_PEARL
                  set-base:
                    material: COMPASS
        """.trimIndent())

        val configuration = load(file)

        configuration.upgradeLayout()
        configuration.validateLayout()
        assertEquals("Existing panel", configuration.screens.overview.title)
        assertEquals(listOf("base", "empty"), GuiPattern.slots(configuration.screens.overview.pattern[0]))
        assertEquals(false, configuration.screens.overview.items.containsKey("set-base"))
        assertEquals("NAME_TAG", configuration.screens.relations.items.getValue("entry").material)
    }

    @Test
    fun `previous stock purchase layout expands and keeps customized item text`() {
        val configuration = PanelConfiguration()
        val screen = configuration.screens.expansion
        val entries = "{entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry}"
        screen.rows = 5
        screen.pattern = listOf(entries, entries, entries,
            "{empty} {empty} {empty} {empty} {summary} {empty} {empty} {empty} {empty}",
            "{previous} {empty} {empty} {empty} {page} {empty} {empty} {next} {back}")
        screen.items.getValue("entry").name = "Custom upgrade"

        configuration.upgradeLayout()
        configuration.validateLayout()

        assertEquals(6, screen.rows)
        assertEquals("Custom upgrade", screen.items.getValue("entry").name)
    }

    @Test
    fun `unknown slots and oversized layouts are rejected`() {
        val configuration = PanelConfiguration()
        configuration.screens.overview.pattern = listOf("{unknown}")
        assertThrows(IllegalArgumentException::class.java, configuration::validateLayout)

        configuration.screens.overview.rows = 7
        assertThrows(IllegalArgumentException::class.java, configuration::validateLayout)
    }

    private fun load(file: File): PanelConfiguration =
        ConfigManager.create(PanelConfiguration::class.java) { config ->
            config.configure { options ->
                options.configurer(YamlBukkitConfigurer(), SerdesCommons())
                options.bindFile(file)
            }
            config.saveDefaults()
            config.load(true)
        }
}
