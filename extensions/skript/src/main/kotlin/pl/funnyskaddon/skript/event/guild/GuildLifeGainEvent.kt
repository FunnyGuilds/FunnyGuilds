package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Life Gain")
@Description("Called when a guild gains a life")
@Since("3.0.0")
@Examples(
    "on guild life gain:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildLifeGainEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildLifeGainEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildLifeGainEvent::class.java, "Guild Life Gain", "guild life gain")
        }
    }

}
