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

@Name("Add Assists")
@Since("3.0.0")
@Description("Adds assists to the player's ranking")
@Examples(
    "add 5 assists to player's ranking",
)
class PlayerAddAssistsEffect : PlayerEffect<Number>(true) {

    companion object {
        init {
            registerEffect<PlayerAddAssistsEffect>(
                "add %number% assist[s] to %offlineplayer%['s] [(rank|ranking)]"
            )
        }
    }

    override fun execute(event: Event) {
        event.getUser(playerExpression)
            .peek { user ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val assistsChangeEvent = AssistsChangeEvent(FunnyEvent.EventCause.CONSOLE, user, user, value)
                        if (!SimpleEventHandler.handle(assistsChangeEvent)) {
                            return@valuePeek
                        }

                        user.rank.updateAssists { assists -> assists + assistsChangeEvent.assistsChange }
                    }
            }
    }

}
