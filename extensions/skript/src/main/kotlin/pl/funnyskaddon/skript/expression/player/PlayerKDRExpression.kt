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

@Name("Player KDR")
@Since("2.0.0")
@Description("Returns the KDR of a player")
@Examples(
    "send \"%player kdr%\"",
    "set {_kdr} to player's kdr"
)
class PlayerKDRExpression : PlayerExpression<Number>(Number::class.javaObjectType) {

    companion object {
        init {
            registerExpression<PlayerKDRExpression, Number>(
                PROPERTY,
                "%offlineplayer%['s] [(rank|ranking)] (kdr|kills to deaths ratio)"
            )
        }
    }

    override fun get(event: Event): Array<Float> {
        return event.getUser(playerExpression)
            .map { user -> user.rank.kdr }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0F))

    }


}