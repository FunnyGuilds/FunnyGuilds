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

@Name("Remove Deaths")
@Description("Removes deaths from the player's ranking")
@Since("2.0.0")
@Examples(
    "remove 7 deaths from player's ranking",
)
class PlayerRemoveDeathsEffect : PlayerEffect<Number>(true) {

    companion object {
        init {
            registerEffect<PlayerRemoveDeathsEffect>(
                "remove %number% death[s] from %offlineplayer%['s] [(rank|ranking)]"
            )
        }
    }

    override fun execute(event: Event) {
        event.getUser(playerExpression)
            .peek { user ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val deathsChangeEvent = DeathsChangeEvent(FunnyEvent.EventCause.CONSOLE, user, user, -value)
                        if (!SimpleEventHandler.handle(deathsChangeEvent)) {
                            return@valuePeek
                        }

                        user.rank.updateDeaths { deaths -> deaths + deathsChangeEvent.deathsChange }
                    }
            }
    }

}