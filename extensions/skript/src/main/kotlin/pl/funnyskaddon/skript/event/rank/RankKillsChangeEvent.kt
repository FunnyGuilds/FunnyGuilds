package pl.funnyskaddon.skript.event.rank

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import net.dzikoysk.funnyguilds.event.rank.KillsChangeEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Kills Change")
@Description("Called when the player's ranking kills change")
@Since("2.0.0")
@Examples(
    "on player kills change:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%affected player%\"",
    "    send \"%change%\""
)
class RankKillsChangeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(RankKillsChangeEvent::class.java, KillsChangeEvent::class.java, "Kills Change", "[player] [(rank|ranking)] kills change")
        }
    }

}
