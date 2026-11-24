package pl.funnyskaddon.skript.effect.player

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.rank.KillsChangeEvent
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.PlayerEffect
import pl.funnyskaddon.skript.getUser
import pl.funnyskaddon.skript.getValue
import pl.funnyskaddon.skript.registration.Registration.registerEffect

@Name("Remove Kills")
@Description("Removes kills from the player's ranking")
@Since("2.0.0")
@Examples(
    "remove 7 kills from player's ranking",
)
class PlayerRemoveKillsEffect : PlayerEffect<Number>(true) {

    companion object {
        init {
            registerEffect<PlayerRemoveKillsEffect>(
                "remove %number% kill[s] from %offlineplayer%['s] [(rank|ranking)]"
            )
        }
    }

    override fun execute(event: Event) {
        event.getUser(playerExpression)
            .peek { user ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val killsChangeEvent = KillsChangeEvent(FunnyEvent.EventCause.CONSOLE, user, user, -value)
                        if (!SimpleEventHandler.handle(killsChangeEvent)) {
                            return@valuePeek
                        }

                        user.rank.updateKills { kills -> kills + killsChangeEvent.killsChange }
                    }
            }
    }

}