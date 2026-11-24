package pl.funnyskaddon.skript.expression.player

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.event.Event
import pl.funnyskaddon.skript.expression.PlayerExpression
import pl.funnyskaddon.skript.getUser
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression

@Name("Player Logouts")
@Since("3.0.0")
@Description("Returns the number of logouts of a player")
@Examples(
    "send \"%player logouts%\"",
    "set {_logouts} to player's logouts"
)
class PlayerLogoutsExpression : PlayerExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<PlayerLogoutsExpression, Int>(
                PROPERTY,
                "%offlineplayer%['s] [(rank|ranking)] logouts [(amount|count)]"
            )
        }
    }

    override fun get(event: Event): Array<Int> {
        return event.getUser(playerExpression)
            .map { user -> user.rank.logouts }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0))
    }


}
