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

@Name("Player Kills")
@Since("2.0.0")
@Description("Returns the number of kills of a player")
@Examples(
    "send \"%player kills%\"",
    "set {_kills} to player's kills"
)
class PlayerKillsExpression : PlayerExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<PlayerKillsExpression, Int>(
                PROPERTY,
                "%offlineplayer%['s] [(rank|ranking)] kills [(amount|count)]"
            )
        }
    }

    override fun get(event: Event): Array<Int> {
        return event.getUser(playerExpression)
            .map { user -> user.rank.kills }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0))
    }


}