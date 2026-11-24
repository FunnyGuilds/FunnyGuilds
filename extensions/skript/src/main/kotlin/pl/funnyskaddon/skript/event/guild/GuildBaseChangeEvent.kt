package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Base Change")
@Description("Called when the guild base location changes")
@Since("2.0.0")
@Examples(
    "on guild base change:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\"",
    "    send \"%new base location%\""
)
class GuildBaseChangeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildBaseChangeEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildBaseChangeEvent::class.java, "Guild Base Change", "guild base change")
        }
    }

}
