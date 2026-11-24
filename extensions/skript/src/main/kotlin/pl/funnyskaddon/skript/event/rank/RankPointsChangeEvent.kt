package pl.funnyskaddon.skript.event.rank

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Points Change")
@Description("Called when the player's ranking points change")
@Since("2.0.0")
@Examples(
    "on player points change:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%affected player%\"",
    "    send \"%change%\""
)
class RankPointsChangeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(RankPointsChangeEvent::class.java, PointsChangeEvent::class.java, "Points Change", "[player] [(rank|ranking)] points change")
        }
    }

}
