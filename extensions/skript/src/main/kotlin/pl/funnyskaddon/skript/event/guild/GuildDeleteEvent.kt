package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Delete")
@Description("Called when a guild is deleted")
@Since("2.0.0")
@Examples(
    "on guild delete:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildDeleteEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildDeleteEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent::class.java, "Guild Delete", "guild (delete|deletion)")
        }
    }

}
