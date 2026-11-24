package pl.funnyskaddon.skript.effect.player

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.PlayerEffect
import pl.funnyskaddon.skript.getUser
import pl.funnyskaddon.skript.getValue
import pl.funnyskaddon.skript.registration.Registration.registerEffect

@Name("Remove Points")
@Description("Removes points from the player's ranking")
@Since("2.0.0")
@Examples(
    "remove 7 points from player's ranking",
)
class PlayerRemovePointsEffect : PlayerEffect<Number>(true) {

    companion object {
        init {
            registerEffect<PlayerRemovePointsEffect>(
                "remove %number% point[s] from %offlineplayer%['s] [(rank|ranking)]"
            )
        }
    }

    override fun execute(event: Event) {
        event.getUser(playerExpression)
            .peek { user ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val pointsChangeEvent = PointsChangeEvent(FunnyEvent.EventCause.CONSOLE, user, user, -value)
                        if (!SimpleEventHandler.handle(pointsChangeEvent)) {
                            return@valuePeek
                        }

                        user.rank.updatePoints { points -> points + pointsChangeEvent.pointsChange }
                    }
            }
    }

}