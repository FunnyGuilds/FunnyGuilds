package pl.funnyskaddon.skript.expression.player

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.event.Event
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.PlayerExpression
import pl.funnyskaddon.skript.getUser

@Name("Player Points")
@Since("2.0.0")
@Description("Returns the number of ranking points of a player")
@Examples(
    "send \"%player points%\"",
    "set {_points} to player's points"
)
class PlayerPointsExpression : PlayerExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<PlayerPointsExpression, Int>(
                PROPERTY,
                "%offlineplayer%['s] [(rank|ranking)] points [(amount|count)]"
            )
        }
    }

    override fun get(event: Event): Array<Int> {
        return event.getUser(playerExpression)
            .map { user -> user.rank.points }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0))
    }


}