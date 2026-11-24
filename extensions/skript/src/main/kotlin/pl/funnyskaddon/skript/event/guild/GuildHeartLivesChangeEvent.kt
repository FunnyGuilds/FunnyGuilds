package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Heart Lives Change")
@Description("Called when the number of a guild heart's lives changes")
@Since("3.0.0")
@Examples(
    "on guild heart lives change:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildHeartLivesChangeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildHeartLivesChangeEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildHeartLivesChangeEvent::class.java, "Guild Heart Lives Change", "guild heart lives change")
        }
    }

}
