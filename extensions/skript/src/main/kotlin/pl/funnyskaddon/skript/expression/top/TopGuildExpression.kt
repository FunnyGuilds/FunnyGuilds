package pl.funnyskaddon.skript.expression.top

import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.rank.DefaultTops
import org.bukkit.event.Event
import pl.funnyskaddon.skript.expression.TopExpression
import pl.funnyskaddon.skript.getValue

@Name("Guild In Position")
@Since("2.0.0")
@Description("Returns the guild at the given position in the ranking")
@Examples(
    "send \"%guild in position 1%\"",
    "set {_guildOnPosition} to guild in position 4"
)
class TopGuildExpression : TopExpression<Guild>(Guild::class.java) {

    companion object {
        init {
            registerExpression<TopGuildExpression, Guild>(
                PROPERTY,
                "guild in position %number% [of (top|rank|ranking)]",
                "guild in position %number% [of %-string% (top|rank|ranking)]"
            )
        }
    }

    override fun get(event: Event): Array<Guild>? {
        return event.getValue(positionExpression)
            .map(Number::toInt)
            .flatMap { position ->
                val type: String = event.getValue(typeExpression).orElse(DefaultTops.GUILD_AVG_POINTS_TOP)
                FunnyGuilds.getInstance().guildRankManager.getGuild(
                    type,
                    position
                )
            }
            .map { value -> arrayOf(value) }
            .orNull()
    }


    override fun toString(e: Event?, debug: Boolean): String {
        if (e != null) {
            val type: String = e.getValue(typeExpression).orElse(DefaultTops.GUILD_AVG_POINTS_TOP)
            return "guild at position ${positionExpression.toString(e, debug)} in $type top"
        }
        return "guild at position of top"
    }

}