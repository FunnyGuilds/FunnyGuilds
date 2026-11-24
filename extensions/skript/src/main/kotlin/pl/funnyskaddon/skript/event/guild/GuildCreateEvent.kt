package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Create")
@Description("Called when a guild is created")
@Since("2.0.0")
@Examples(
    "on guild create:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildCreateEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildCreateEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildCreateEvent::class.java, "Guild Create", "guild (create|creation)")
        }
    }

}
