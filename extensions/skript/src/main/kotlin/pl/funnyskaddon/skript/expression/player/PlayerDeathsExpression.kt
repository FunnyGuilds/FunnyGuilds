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

@Name("Player Deaths")
@Since("2.0.0")
@Description("Returns the number of deaths of a player")
@Examples(
    "send \"%player deaths%\"",
    "set {_deaths} to player's deaths"
)
class PlayerDeathsExpression : PlayerExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<PlayerDeathsExpression, Int>(
                PROPERTY,
                "%offlineplayer%['s] [(rank|ranking)] deaths [(amount|count)]"
            )
        }
    }

    override fun get(event: Event): Array<Int> {
        return event.getUser(playerExpression)
            .map { user -> user.rank.deaths }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0))
    }


}