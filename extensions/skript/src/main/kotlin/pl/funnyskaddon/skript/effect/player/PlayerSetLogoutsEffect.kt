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

@Name("Set Logouts")
@Since("3.0.0")
@Description("Sets the logouts in the player's ranking")
@Examples(
    "set number of player's ranking logouts to 2",
)
class PlayerSetLogoutsEffect : PlayerEffect<Number>(false) {

    companion object {
        init {
            registerEffect<PlayerSetLogoutsEffect>(
                "set [(number|amount) of] %offlineplayer%['s] [(rank|ranking)] logouts to %number%"
            )
        }
    }

    override fun execute(event: Event) {
        event.getUser(playerExpression)
            .peek { user ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val logoutsChangeEvent =
                            LogoutsChangeEvent(FunnyEvent.EventCause.CONSOLE, user, user, value - user.rank.logouts)
                        if (!SimpleEventHandler.handle(logoutsChangeEvent)) {
                            return@valuePeek
                        }

                        user.rank.logouts = user.rank.logouts + logoutsChangeEvent.logoutsChange
                    }
            }
    }

}
