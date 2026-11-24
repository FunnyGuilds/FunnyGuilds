package pl.funnyskaddon.skript.effect.player

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.rank.DeathsChangeEvent
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.PlayerEffect
import pl.funnyskaddon.skript.getUser
import pl.funnyskaddon.skript.getValue
import pl.funnyskaddon.skript.registration.Registration.registerEffect

@Name("Set Deaths")
@Description("Sets the deaths in the player's ranking")
@Since("2.0.0")
@Examples(
    "set number of player's ranking deaths to 2",
)
class PlayerSetDeathsEffect : PlayerEffect<Number>(false) {

    companion object {
        init {
            registerEffect<PlayerSetDeathsEffect>(
                "set [(number|amount) of] %offlineplayer%['s] [(rank|ranking)] deaths to %number%"
            )
        }
    }

    override fun execute(event: Event) {
        event.getUser(playerExpression)
            .peek { user ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val deathsChangeEvent =
                            DeathsChangeEvent(FunnyEvent.EventCause.CONSOLE, user, user, value - user.rank.deaths)
                        if (!SimpleEventHandler.handle(deathsChangeEvent)) {
                            return@valuePeek
                        }

                        user.rank.deaths = user.rank.deaths + deathsChangeEvent.deathsChange
                    }
            }
    }

}