package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Tag Change")
@Description("Called when the guild tag changes")
@Since("2.0.0")
@Examples(
    "on guild tag change:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\"",
    "    send \"%old tag%\"",
    "    send \"%old name%\""
)
class GuildTagChangeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildTagChangeEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildPreTagChangeEvent::class.java, "Guild Tag Change", "guild tag change")
        }
    }

}
