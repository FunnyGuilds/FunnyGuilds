package pl.funnyskaddon.skript.expression.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.event.Event
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild

@Name("Guild Points")
@Since("2.0.0")
@Description("Returns the number of points of a guild")
@Examples(
    "send \"%\"\"FajnaGildia\"\" guild points%\"",
    "set {_points} to \"FajnaGildia\" guild points"
)
class GuildPointsExpression : GuildExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<GuildPointsExpression, Int>(
                PROPERTY,
                "%guild%['s]  [(rank|ranking)] points [(amount|count)]",
                "%string%['s] [guild] [(rank|ranking)] points [(amount|count)]",
                "%offlineplayer%['s] guild['s]  [(rank|ranking)] points [(amount|count)]",
                "%location%['s] guild['s]  [(rank|ranking)] points [(amount|count)]",
                "%block%['s] guild['s]  [(rank|ranking)] points [(amount|count)]",
                "%object%['s] guild['s]  [(rank|ranking)] points [(amount|count)]"
            )
        }
    }

    override fun get(event: Event): Array<Int> {
        return event.getGuild(guildExpression)
            .map { guild -> guild.rank.averagePoints }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0))
    }


}