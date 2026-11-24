package pl.funnyskaddon.skript.event.rank

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import net.dzikoysk.funnyguilds.event.rank.AssistsChangeEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Logouts Change")
@Description("Called when the player's ranking logouts change")
@Since("2.0.0")
@Examples(
    "on player logouts change:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%affected player%\"",
    "    send \"%change%\""
)
class RankLogoutsChangeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(RankLogoutsChangeEvent::class.java, AssistsChangeEvent::class.java, "Logouts Change", "[player] [(rank|ranking)] logouts change")
        }
    }

}
