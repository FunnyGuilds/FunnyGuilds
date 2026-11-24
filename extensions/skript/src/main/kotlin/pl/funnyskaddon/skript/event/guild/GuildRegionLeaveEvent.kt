package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Region Leave")
@Description("Called when a player leaves the guild region")
@Since("2.0.0")
@Examples(
    "on guild region leave:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildRegionLeaveEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildRegionLeaveEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildRegionLeaveEvent::class.java, "Guild Region Leave", "guild region leave")
        }
    }

}
