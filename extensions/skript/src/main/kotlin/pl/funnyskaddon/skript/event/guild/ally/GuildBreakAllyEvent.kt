package pl.funnyskaddon.skript.event.guild.ally

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Break Ally")
@Description("Called when a guild breaks an alliance")
@Since("2.0.0")
@Examples(
    "on guild break ally:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\"",
    "    send \"%ally%\""
)
class GuildBreakAllyEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildBreakAllyEvent::class.java, net.dzikoysk.funnyguilds.event.guild.ally.GuildBreakAllyEvent::class.java, "Guild Break Ally", "guild break ally")
        }
    }

}
