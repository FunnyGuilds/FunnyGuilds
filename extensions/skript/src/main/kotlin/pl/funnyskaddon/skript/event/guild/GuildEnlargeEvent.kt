package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Enlarge")
@Description("Called when the guild enlargement level changes")
@Since("2.0.0")
@Examples(
    "on guild enlarge:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildEnlargeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildEnlargeEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildEnlargeEvent::class.java, "Guild Enlarge", "guild enlarge")
        }
    }

}
