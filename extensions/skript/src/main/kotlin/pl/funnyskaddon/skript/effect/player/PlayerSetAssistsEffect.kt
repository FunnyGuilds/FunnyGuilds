package pl.funnyskaddon.skript.effect.player

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.rank.AssistsChangeEvent
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.PlayerEffect
import pl.funnyskaddon.skript.getUser
import pl.funnyskaddon.skript.getValue
import pl.funnyskaddon.skript.registration.Registration.registerEffect

@Name("Set Assists")
@Since("3.0.0")
@Description("Sets the assists in the player's ranking")
@Examples(
    "set number of player's ranking assists to 2",
)
class PlayerSetAssistsEffect : PlayerEffect<Number>(false) {

    companion object {
        init {
            registerEffect<PlayerSetAssistsEffect>(
                "set [(number|amount) of] %offlineplayer%['s] [(rank|ranking)] assists to %number%"
            )
        }
    }

    override fun execute(event: Event) {
        event.getUser(playerExpression)
            .peek { user ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val assistsChangeEvent =
                            AssistsChangeEvent(FunnyEvent.EventCause.CONSOLE, user, user, value - user.rank.assists)
                        if (!SimpleEventHandler.handle(assistsChangeEvent)) {
                            return@valuePeek
                        }

                        user.rank.assists = user.rank.assists + assistsChangeEvent.assistsChange
                    }
            }
    }

}
