package pl.funnyskaddon.skript.effect.player

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.rank.LogoutsChangeEvent
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.PlayerEffect
import pl.funnyskaddon.skript.getUser
import pl.funnyskaddon.skript.getValue
import pl.funnyskaddon.skript.registration.Registration.registerEffect

@Name("Remove Logouts")
@Since("3.0.0")
@Description("Removes logouts from the player's ranking")
@Examples(
    "remove 7 logouts from player's ranking",
)
class PlayerRemoveLogoutsEffect : PlayerEffect<Number>(true) {

    companion object {
        init {
            registerEffect<PlayerRemoveLogoutsEffect>(
                "remove %number% logout[s] from %offlineplayer%['s] [(rank|ranking)]"
            )
        }
    }

    override fun execute(event: Event) {
        event.getUser(playerExpression)
            .peek { user ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val logoutsChangeEvent = LogoutsChangeEvent(FunnyEvent.EventCause.CONSOLE, user, user, -value)
                        if (!SimpleEventHandler.handle(logoutsChangeEvent)) {
                            return@valuePeek
                        }

                        user.rank.updateLogouts { logouts -> logouts + logoutsChangeEvent.logoutsChange }
                    }
            }
    }

}
