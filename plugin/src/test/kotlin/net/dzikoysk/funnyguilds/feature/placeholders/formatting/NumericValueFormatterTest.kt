package net.dzikoysk.funnyguilds.feature.placeholders.formatting

import net.dzikoysk.funnyguilds.config.NumberRange
import net.dzikoysk.funnyguilds.config.RangeFormatting
import net.dzikoysk.funnyguilds.config.sections.TopConfiguration
import net.dzikoysk.funnyguilds.config.sections.ValueFormattingConfiguration
import net.dzikoysk.funnyguilds.shared.RankFormatter
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Locale

class NumericValueFormatterTest {

    private fun formatter(vararg rules: String) = NumericValueFormatter(rules.map(::RangeFormatting), "test")
    private fun text(component: Component) = PlainTextComponentSerializer.plainText().serialize(component)

    @Test
    fun `keeps spaces and applies real legacy hex and minimessage styles`() {
        for (template in listOf("&a{VALUE} ranking points", "&#12ab34{VALUE} ranking points", "<green>{VALUE} ranking points</green>")) {
            val result = formatter("0-* $template").format(1500)
            assertEquals("1500 ranking points", text(result))
            assertEquals(ComponentUtil.colored(template.replace("{VALUE}", "1500")).compact(), result.compact())
        }
    }

    @Test
    fun `uses first match including duplicate ranges`() {
        val result = formatter("0-10 first {VALUE}", "0-10 duplicate", "5-20 overlapping")
        assertEquals("first 7", text(result.format(7)))
        assertEquals("overlapping", text(result.format(11)))
        assertEquals("21", text(result.format(21)))
    }

    @Test
    fun `matches original value before rounding with exclusive decimal upper bound`() {
        val rules = formatter("0.0-1.0 low {VALUE}", "1.0-2.0 medium {VALUE}", "2.0-* high {VALUE}")
        assertEquals("low 0.00", text(rules.format(0.0)))
        assertEquals("medium 1.00", text(rules.format(1.0)))
        assertEquals("medium 2.00", text(rules.format(1.999)))
        assertEquals("high 2.00", text(rules.format(2.0)))
        assertTrue(NumberRange.parseStrict("0-1").contains(1))
        assertFalse(NumberRange.parseStrict("0.0-1.0").contains(1))
    }

    @Test
    fun `distinguishes adjacent large integers and handles signed limits`() {
        val rules = formatter("*--1 negative", "16777216-16777216 first", "16777217-16777217 second", "2147483647-* maximum")
        assertEquals("negative", text(rules.format(Int.MIN_VALUE)))
        assertEquals("first", text(rules.format(16777216)))
        assertEquals("second", text(rules.format(16777217)))
        assertEquals("maximum", text(rules.format(Int.MAX_VALUE)))
        assertTrue(NumberRange.parseStrict("-*--101").contains(Int.MIN_VALUE))
    }

    @Test
    fun `plain output and gaps are independent of default locale without narrowing double`() {
        val previous = Locale.getDefault()
        try {
            for (locale in listOf(Locale.forLanguageTag("pl-PL"), Locale.US)) {
                Locale.setDefault(locale)
                assertEquals("1.25", text(formatter().format(1.25)))
                assertEquals("1.25", text(formatter("0-0 zero").format(1.25)))
                assertEquals("16777217.25", text(formatter().format(16777217.25)))
                assertEquals("0", text(formatter().format(0)))
            }
        } finally {
            Locale.setDefault(previous)
        }
    }

    @Test
    fun `rejects malformed reversed nonfinite and empty ranges`() {
        for (rule in listOf("bad &a{VALUE}", "0-1", "0-1   ", "2-1 value", "1.0-1.0 value", "NaN-2 value", "0-Infinity value", "0-1.2.3 value", "0-2147483648 value")) {
            assertThrows(IllegalArgumentException::class.java, { RangeFormatting(rule) }, rule)
        }
        assertThrows(IllegalArgumentException::class.java) { RangeFormatting(Double.NaN, 2, "value") }
    }

    @Test
    fun `compiled rules do not retain mutable template state or expand foreign tokens`() {
        val rule = RangeFormatting("0-* &a{VALUE} {POINTS}")
        val compiled = NumericValueFormatter(listOf(rule), "test")
        rule.value = "changed"
        assertEquals("7 {POINTS}", text(compiled.format(7)))
    }

    @Test
    fun `top overrides distinguish absent empty and unmatched lists`() {
        val config = ValueFormattingConfiguration()
        config.user = mapOf("kdr" to listOf(RangeFormatting("0-* general {VALUE}")))
        config.guild = config.user
        val top = TopConfiguration()
        top.format.ptop = "{VALUE-FORMAT} / {VALUE}"
        top.format.gtop = top.format.ptop
        for (guild in listOf(false, true)) {
            for ((overrides, expected) in listOf(
                emptyMap<String, List<RangeFormatting>>() to "general 1.50 / raw",
                mapOf("kdr" to emptyList<RangeFormatting>()) to "1.50 / raw",
                mapOf("kdr" to listOf(RangeFormatting("0-0 zero"))) to "1.50 / raw",
                mapOf("kdr" to listOf(RangeFormatting("0-* override {VALUE}"))) to "override 1.50 / raw"
            )) {
                top.format.ptopValueFormatting = overrides
                top.format.gtopValueFormatting = overrides
                assertEquals(expected, text(ValueFormatting(config, top).top(guild, "KDR", 1.5, "raw")))
            }
        }
    }

    @Test
    fun `top replacement does not recurse into tokens in a rule and preserves surrounding styles`() {
        val config = ValueFormattingConfiguration()
        config.user = mapOf("points" to listOf(RangeFormatting("0-* &a{VALUE} {VALUE-FORMAT}")))
        val top = TopConfiguration()
        top.format.ptopValueFormatting = emptyMap()
        top.format.ptop = "&7[{VALUE-FORMAT}&7] &cend"
        val result = ValueFormatting(config, top).top(false, "points", 7, "7")
        assertEquals("[7 {VALUE-FORMAT}] end", text(result))
        assertEquals(ComponentUtil.toLegacy(ComponentUtil.colored("&7[&a7 {VALUE-FORMAT}&7] &cend")), ComponentUtil.toLegacy(result))
    }

    @Test
    fun `signed point change formatting keeps its separate contract`() {
        val changes = listOf(RangeFormatting("*--1 &c-{CHANGE}"), RangeFormatting("0-0 &7{CHANGE}"), RangeFormatting("1-* &a+{CHANGE}"))
        assertEquals("§c-7", RankFormatter.formatPointsChange(-7, changes))
        assertEquals("§70", RankFormatter.formatPointsChange(0, changes))
        assertEquals("§a+7", RankFormatter.formatPointsChange(7, changes))
    }
}
