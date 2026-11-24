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

@Name("Add Logouts")
@Since("3.0.0")
@Description("Adds logouts to the player's ranking")
@Examples(
    "add 5 logouts to player's ranking",
)
class PlayerAddLogoutsEffect : PlayerEffect<Number>(true) {

    companion object {
        init {
            registerEffect<PlayerAddLogoutsEffect>(
                "add %number% logout[s] to %offlineplayer%['s] [(rank|ranking)]"
            )
        }
    }

    override fun execute(event: Event) {
        event.getUser(playerExpression)
            .peek { user ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val logoutsChangeEvent = LogoutsChangeEvent(FunnyEvent.EventCause.CONSOLE, user, user, value)
                        if (!SimpleEventHandler.handle(logoutsChangeEvent)) {
                            return@valuePeek
                        }

                        user.rank.updateLogouts { logouts -> logouts + logoutsChangeEvent.logoutsChange }
                    }
            }
    }

}
