package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Chat Event")
@Description("Called when a player sends a message on the guild chat")
@Since("2.0.0")
@Examples(
    "on guild chat:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%receivers%\"",
    "    send \"%message%\"",
    "    send \"%type%\""
)
class GuildChatEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildChatEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildChatEvent::class.java, "Guild Chat Event", "guild chat")
        }
    }

}
