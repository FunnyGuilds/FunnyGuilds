package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Heart Interact")
@Description("Called when a player interacts with a guild heart")
@Since("3.0.0")
@Examples(
    "on guild heart interact:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildHeartInteractEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildHeartInteractEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent::class.java, "Guild Heart Interact", "guild heart (interact|click)")
        }
    }

}
