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

@Name("Remove Assists")
@Since("3.0.0")
@Description("Removes assists from the player's ranking")
@Examples(
    "remove 7 assists from player's ranking",
)
class PlayerRemoveAssistsEffect : PlayerEffect<Number>(true) {

    companion object {
        init {
            registerEffect<PlayerRemoveAssistsEffect>(
                "remove %number% assist[s] from %offlineplayer%['s] [(rank|ranking)]"
            )
        }
    }

    override fun execute(event: Event) {
        event.getUser(playerExpression)
            .peek { user ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val assistsChangeEvent = AssistsChangeEvent(FunnyEvent.EventCause.CONSOLE, user, user, -value)
                        if (!SimpleEventHandler.handle(assistsChangeEvent)) {
                            return@valuePeek
                        }

                        user.rank.updateAssists { assists -> assists + assistsChangeEvent.assistsChange }
                    }
            }
    }

}
