package net.dzikoysk.funnyguilds.feature.panel

import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertSame

class PanelAppearanceTest {

    @Test
    fun `module renders placeholders and passes the explicit material to the shared icon builder`() {
        val icon = mock(ItemStack::class.java)
        val meta = mock(ItemMeta::class.java)
        `when`(icon.itemMeta).thenReturn(meta)
        val definition = GuildItemDefinition("PAPER")
        val appearance = PanelAppearance(MiniMessage.miniMessage()) { template, override ->
            assertSame(definition, template)
            assertEquals("LAVA_BUCKET", override)
            icon
        }

        val values = mapOf("NAME" to "<red>Rola")
        val title = appearance.render("<green>{NAME}", values)

        assertEquals("<red>Rola", PlainTextComponentSerializer.plainText().serialize(title))
        assertSame(icon, appearance.icon(definition, values, emptyList(), "LAVA_BUCKET"))
        verify(icon).setItemMeta(meta)
    }
}
