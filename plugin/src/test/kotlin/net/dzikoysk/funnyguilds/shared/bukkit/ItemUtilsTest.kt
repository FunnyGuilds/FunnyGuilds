package net.dzikoysk.funnyguilds.shared.bukkit

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ItemUtilsTest {

    @Test
    fun `hex item name stores color on the text instead of an empty parent`() {
        val expected = Component.text("nazwa", TextColor.color(0x12ab34))
            .decoration(TextDecoration.ITALIC, false)

        assertEquals(expected, ItemUtils.coloredWithoutItalics("<#12ab34>nazwa"))
    }

    @Test
    fun `item names preserve text and hex colors`() {
        val names = listOf(
            0x123456 to "Guild key",
            0xabcdef to "Magic dust",
            0xfedcba to "Złoty klucz"
        )

        for ((color, name) in names) {
            val textColor = TextColor.color(color)
            val expected = Component.text(name, textColor).decoration(TextDecoration.ITALIC, false)

            assertEquals(expected, ItemUtils.coloredWithoutItalics("<${textColor.asHexString()}>$name"))
        }
    }

    @Test
    fun `MiniMessage and legacy hex item names have identical components`() {
        val expected = ItemUtils.coloredWithoutItalics("<#12ab34>nazwa")

        assertEquals(expected, ItemUtils.coloredWithoutItalics("&#12ab34nazwa"))
        assertEquals(expected, ItemUtils.coloredWithoutItalics("&x&1&2&a&b&3&4nazwa"))
    }

    @Test
    fun `item text keeps explicit and nested formatting`() {
        for (text in listOf(
            "<italic><#12ab34>nazwa</italic>",
            "<#12ab34><bold>Guild</bold> key",
            "<#12ab34>First <#abcdef>second",
            "<#12ab34>First<reset> second",
            "<italic>First</italic> second"
        )) {
            val expected = MiniMessage.miniMessage().deserialize(text)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .compact()

            assertEquals(expected, ItemUtils.coloredWithoutItalics(text), text)
        }
    }
}
