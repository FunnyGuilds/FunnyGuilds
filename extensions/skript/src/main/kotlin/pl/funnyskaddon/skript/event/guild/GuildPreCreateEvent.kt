package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Pre Create")
@Description("Called before a guild is created. Cancellable")
@Since("3.0.0")
@Examples(
    "on guild pre create:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildPreCreateEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildPreCreateEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildPreCreateEvent::class.java, "Guild Pre Create", "guild pre[-] (create|creation)")
        }
    }

}
