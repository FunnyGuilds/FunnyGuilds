package net.dzikoysk.funnyguilds.feature.command

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnyguilds.config.sections.CommandsConfiguration
import net.dzikoysk.funnyguilds.feature.panel.PanelCommand
import net.dzikoysk.funnyguilds.feature.panel.PanelModule
import net.dzikoysk.funnyguilds.module.api.ExtensionCommands.Command
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito.mock
import java.nio.file.Path
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class ExtensionCommandRegistryTest {

    class SampleConfig : OkaeriConfig() {
        @JvmField
        var commands = CommandsConfiguration()
    }

    @Test
    fun `the panel declares its command and the command calls the extension entry point`() {
        val registry = ExtensionCommandRegistry()
        val player = mock(Player::class.java)
        val callers = mutableListOf<Player>()
        val module = PanelModule(mock(Plugin::class.java), {
            callers.add(it)
            Optional.empty()
        }, {}, { error("unused") }, { _, _ -> error("unused") })
        module.registerCommands(registry)
        val command = registry.entries().single()
        assertEquals("guild-panel.panel", command.id())
        assertEquals("panel", command.name())
        val component = command.component() as PanelCommand
        val annotation = component.javaClass.getMethod("execute", Player::class.java).getAnnotation(FunnyCommand::class.java)
        assertEquals("funnyguilds.panel", annotation.permission)
        assertEquals("\${extension.guild-panel.panel.name}", annotation.name)
        assertFailsWith<IllegalArgumentException> { module.registerCommands(registry) }

        component.execute(player)
        assertEquals(listOf(player), callers)
    }

    @Test
    fun `extension settings override legacy values field by field`() {
        val command = Command("guild-panel.panel", "panel", listOf("gp"), mock(PanelCommand::class.java))
        val legacy = CommandsConfiguration.FunnyCommand("gpanel", listOf("menu"), false)
        val override = CommandsConfiguration.ExtensionCommand().apply {
            name = "guildmenu"
            aliases = emptyList()
        }

        val defaults = ExtensionCommandRegistry.settings(command, null, null)
        assertEquals("panel", defaults.name)
        assertEquals(listOf("gp"), defaults.aliases)
        val merged = ExtensionCommandRegistry.settings(command, override, legacy)
        assertEquals("guildmenu", merged.name)
        assertEquals(emptyList(), merged.aliases)
        assertFalse(merged.enabled)
    }

    @Test
    fun `extension settings survive a config save and reload`(@TempDir tempDir: Path) {
        val file = tempDir.resolve("config.yml").toFile()
        file.writeText(
            """
            commands:
              panel:
                name: guildpanel
                aliases: [menu]
                enabled: true
              extensions:
                'guild-panel.panel':
                  aliases: []
                  enabled: false
            """.trimIndent()
        )

        val config = ConfigManager.create(SampleConfig::class.java) { cfg ->
            cfg.configure { options ->
                options.configurer(YamlBukkitConfigurer(), SerdesCommons())
                options.bindFile(file)
            }
            cfg.load(true)
        }
        config.load()

        val command = Command("guild-panel.panel", "panel", listOf(), mock(PanelCommand::class.java))
        val settings = ExtensionCommandRegistry.settings(command, config.commands.extensions.getValue(command.id()), config.commands.panel)
        assertEquals("guildpanel", settings.name)
        assertEquals(emptyList(), settings.aliases)
        assertFalse(settings.enabled)
    }
}
