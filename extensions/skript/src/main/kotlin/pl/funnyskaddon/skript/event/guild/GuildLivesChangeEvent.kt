package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Lives Change")
@Description("Called when the number of guild lives changes")
@Since("2.0.0")
@Examples(
    "on guild lives change:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\"",
    "    send \"%lives%\""
)
class GuildLivesChangeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildLivesChangeEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent::class.java, "Guild Lives Change", "guild lives change")
        }
    }

}
