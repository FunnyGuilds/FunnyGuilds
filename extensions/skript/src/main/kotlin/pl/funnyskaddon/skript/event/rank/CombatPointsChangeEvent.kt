package pl.funnyskaddon.skript.event.rank

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Combat Points Change")
@Description("Called when ranking points change after combat between two players")
@Since("3.0.0")
@Examples(
    "on combat points change:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%affected player%\""
)
class CombatPointsChangeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(CombatPointsChangeEvent::class.java, net.dzikoysk.funnyguilds.event.rank.CombatPointsChangeEvent::class.java, "Combat Points Change", "combat points change")
        }
    }

}
