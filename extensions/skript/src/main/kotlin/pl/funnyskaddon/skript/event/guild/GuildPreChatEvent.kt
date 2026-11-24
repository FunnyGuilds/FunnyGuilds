package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Pre Chat Event")
@Description("Called when a player sends a message on the guild chat (before other guilds receive the message)")
@Since("2.0.0")
@Examples(
    "on guild pre chat:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%receivers%\"",
    "    send \"%message%\"",
    "    send \"%type%\""
)
class GuildPreChatEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildPreChatEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildPreChatEvent::class.java, "Guild Pre Chat Event", "guild [pre( |-)]chat")
        }
    }

}
