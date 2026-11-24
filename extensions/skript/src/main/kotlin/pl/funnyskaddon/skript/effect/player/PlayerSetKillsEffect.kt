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

@Name("Set Kills")
@Description("Sets the kills in the player's ranking")
@Since("2.0.0")
@Examples(
    "set number of player's ranking kills to 2",
)
class PlayerSetKillsEffect : PlayerEffect<Number>(false) {

    companion object {
        init {
            registerEffect<PlayerSetKillsEffect>(
                "set [(number|amount) of] %offlineplayer%['s] [(rank|ranking)] kills to %number%"
            )
        }
    }

    override fun execute(event: Event) {
        event.getUser(playerExpression)
            .peek { user ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val killsChangeEvent =
                            KillsChangeEvent(FunnyEvent.EventCause.CONSOLE, user, user, value - user.rank.kills)
                        if (!SimpleEventHandler.handle(killsChangeEvent)) {
                            return@valuePeek
                        }

                        user.rank.kills = user.rank.kills + killsChangeEvent.killsChange
                    }
            }
    }

}