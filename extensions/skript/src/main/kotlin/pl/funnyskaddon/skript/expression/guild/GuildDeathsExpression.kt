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

@Name("Guild Deaths")
@Since("2.0.0")
@Description("Returns the number of deaths of a guild (the sum of all deaths of its members)")
@Examples(
    "send \"%\"\"FajnaGildia\"\" guild deaths%\"",
    "set {_deaths} to \"FajnaGildia\" guild deaths"
)
class GuildDeathsExpression : GuildExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<GuildDeathsExpression, Int>(
                PROPERTY,
                "%offlineplayer%['s] guild['s] [(rank|ranking)] deaths [(amount|count)]",
                "%string%['s] [guild] [(rank|ranking)] deaths [(amount|count)]",
                "%guild%['s] [(rank|ranking)] deaths [(amount|count)]",
                "%block%['s] guild['s] [(rank|ranking)] deaths [(amount|count)]",
                "%object%['s] guild['s] [(rank|ranking)] deaths [(amount|count)]",
                "%location%['s] guild['s] [(rank|ranking)] deaths [(amount|count)]"
            )
        }
    }

    override fun get(event: Event): Array<Int> {
        return event.getGuild(guildExpression)
            .map { guild -> guild.rank.averageDeaths }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0))
    }


}