package net.dzikoysk.funnyguilds.shared.bukkit

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ChatUtilsTest {

    @Test
    fun `should color`() {
        val message = "&#ff0000Hello &#00FF00world!"
        val coloredMessage = ChatUtils.colored(message)
        assertEquals("§x§f§f§0§0§0§0Hello §x§0§0§f§f§0§0world!", coloredMessage)
    }

    @Test
    fun `should decolor`() {
        val message = "§x§f§f§0§0§0§0Hello §x§0§0§f§f§0§0world!"
        val decoloredMessage = ChatUtils.decolor(message)
        assertEquals("&#ff0000Hello &#00ff00world!", decoloredMessage)
    }

}