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

@Name("Player Assists")
@Since("3.0.0")
@Description("Returns the number of assists of a player")
@Examples(
    "send \"%player assists%\"",
    "set {_assists} to player's assists"
)
class PlayerAssistsExpression : PlayerExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<PlayerAssistsExpression, Int>(
                PROPERTY,
                "%offlineplayer%['s] [(rank|ranking)] assists [(amount|count)]"
            )
        }
    }

    override fun get(event: Event): Array<Int> {
        return event.getUser(playerExpression)
            .map { user -> user.rank.assists }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0))
    }


}
