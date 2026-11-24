package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Ban")
@Description("Called when a guild gets banned")
@Since("2.0.0")
@Examples(
    "on guild ban:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\"",
    "    send \"%reason%\"",
    "    send \"%time%\""
)
class GuildBanEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildBanEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildBanEvent::class.java, "Guild Ban", "guild ban")
        }
    }

}
