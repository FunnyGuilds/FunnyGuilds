package net.dzikoysk.funnyguilds.config

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.CustomKey
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import net.dzikoysk.funnyguilds.config.sections.TopConfiguration
import net.dzikoysk.funnyguilds.config.sections.ValueFormattingConfiguration
import net.dzikoysk.funnyguilds.config.serdes.RangeFormattingTransformer
import net.dzikoysk.funnyguilds.feature.placeholders.formatting.ValueFormatting
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.io.File

class ValueFormattingConfigurationTest {

    class SampleConfig : OkaeriConfig() {
        // Okaeri load(File) does not close its input stream. Own it so JUnit can clean up on Windows.
        override fun load(file: File): OkaeriConfig = file.inputStream().use { super.load(it) }

        @JvmField
        @CustomKey("value-formatting")
        var valueFormatting = ValueFormattingConfiguration()

        @JvmField
        var top = TopConfiguration()
    }

    private fun create(path: Path): SampleConfig = ConfigManager.create(SampleConfig::class.java) { cfg ->
        cfg.configure { options ->
            options.configurer(YamlBukkitConfigurer(), SerdesCommons())
            options.serdes(RangeFormattingTransformer())
            options.bindFile(path.toFile())
        }
        cfg.saveDefaults()
        cfg.load(true)
    }

    @Test
    fun `defaults custom rules and empty overrides survive save load and reload`(@TempDir directory: Path) {
        val path = directory.resolve("config.yml")
        val config = create(path)
        val defaults = ValueFormatting(config.valueFormatting, config.top)
        assertEquals(7, config.valueFormatting.user.size)
        assertEquals(17, config.valueFormatting.guild.size)
        assertTrue(config.valueFormatting.user.filterKeys { it != "points" }.values.all { it.isEmpty() })
        assertTrue(config.valueFormatting.guild.filterKeys { it != "points" && it != "avg_points" }.values.all { it.isEmpty() })
        assertEquals("1500", PlainTextComponentSerializer.plainText().serialize(defaults.user("points", 1500)))
        config.valueFormatting.guild = linkedMapOf(
            "avg_points" to listOf(RangeFormatting("*--1 &cnegative value"), RangeFormatting("0-* <green>{VALUE} guild points</green>")),
            "kdr" to listOf(RangeFormatting("0.0-1.0 &c{VALUE}"), RangeFormatting("1.0-* &a{VALUE}"))
        )
        config.top.format.gtopValueFormatting = mapOf("avg_points" to emptyList())
        config.save()
        val loaded = create(path)
        assertEquals(config.valueFormatting.guild.keys, loaded.valueFormatting.guild.keys)
        for (metric in config.valueFormatting.guild.keys) {
            for (value in listOf(-1, 0, 1, 1234)) {
                assertEquals(
                    ValueFormatting(config.valueFormatting, config.top).guild(metric, value).compact(),
                    ValueFormatting(loaded.valueFormatting, loaded.top).guild(metric, value).compact()
                )
            }
        }
        assertEquals(config.top.format.gtopValueFormatting, loaded.top.format.gtopValueFormatting)
        val rendered = ValueFormatting(loaded.valueFormatting, loaded.top).guild("avg_points", 1234)
        assertEquals("1234 guild points", PlainTextComponentSerializer.plainText().serialize(rendered))
        loaded.valueFormatting.user = emptyMap()
        loaded.save()
        config.load()
        assertTrue(config.valueFormatting.user.isEmpty())
    }

    @Test
    fun `invalid YAML range reports its config location and list index`(@TempDir directory: Path) {
        val path = directory.resolve("config.yml")
        val config = create(path)
        path.toFile().writeText("""
            value-formatting:
              user:
                kdr:
                  - '0.0-1.0 valid'
                  - 'bad-range invalid'
              guild: {}
              ping: []
        """.trimIndent())
        val error = assertThrows(RuntimeException::class.java) { config.load() }
        val messages = generateSequence<Throwable>(error) { it.cause }.map { it.message.orEmpty() }.joinToString("\n")
        assertTrue(messages.contains("kdr"), messages)
        assertTrue(messages.contains("[1]"), messages)
    }
}
