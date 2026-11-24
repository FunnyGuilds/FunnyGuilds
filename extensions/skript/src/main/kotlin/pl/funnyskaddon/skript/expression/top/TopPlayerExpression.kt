package pl.funnyskaddon.skript.expression.top

import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.flatMapOption
import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.rank.DefaultTops
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import pl.funnyskaddon.extension.getOfflinePlayer
import pl.funnyskaddon.skript.expression.TopExpression
import pl.funnyskaddon.skript.getValue

@Name("Player In Position")
@Since("2.0.0")
@Description("Returns the player at the given position in the ranking")
@Examples(
    "send \"%player in position 1%\"",
    "set {_playerOnPosition} to player in position 4"
)
class TopPlayerExpression : TopExpression<OfflinePlayer>(OfflinePlayer::class.java) {

    companion object {
        init {
            registerExpression<TopPlayerExpression, OfflinePlayer>(
                PROPERTY,
                "player in position %number% [of (top|rank|ranking)]",
                "player in position %number% [of %-string% (top|rank|ranking)]"
            )
        }
    }

    override fun get(event: Event): Array<OfflinePlayer>? {
        return event.getValue(positionExpression)
            .map(Number::toInt)
            .flatMap { position ->
                val type: String = event.getValue(typeExpression).orElse(DefaultTops.USER_POINTS_TOP)
                FunnyGuilds.getInstance().userRankManager.getUser(
                    type,
                    position
                )
            }
            .flatMap(User::getOfflinePlayer)
            .map { value -> arrayOf(value) }
            .orNull()
    }


    override fun toString(e: Event?, debug: Boolean): String {
        if (e != null) {
            val type: String = e.getValue(typeExpression).orElse(DefaultTops.USER_POINTS_TOP)
            return "player at position ${positionExpression.toString(e, debug)} in $type top"
        }
        return "player at position of top"
    }

}