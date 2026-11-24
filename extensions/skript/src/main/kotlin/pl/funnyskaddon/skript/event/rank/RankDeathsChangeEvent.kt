package pl.funnyskaddon.skript.event.rank

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import net.dzikoysk.funnyguilds.event.rank.DeathsChangeEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Deaths Change")
@Description("Called when the player's ranking deaths change")
@Since("2.0.0")
@Examples(
    "on player deaths change:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%affected player%\"",
    "    send \"%change%\""
)
class RankDeathsChangeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(RankDeathsChangeEvent::class.java, DeathsChangeEvent::class.java, "Deaths Change", "[player] [(rank|ranking)] deaths change")
        }
    }

}
