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

@Name("Guild Kills")
@Since("2.0.0")
@Description("Returns the number of kills of a guild (the sum of all kills of its members)")
@Examples(
    "send \"%\"\"FajnaGildia\"\" guild kills%\"",
    "set {_kills} to \"FajnaGildia\" guild kills"
)
class GuildKillsExpression : GuildExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<GuildKillsExpression, Int>(
                PROPERTY,
                "%guild%['s] [(rank|ranking)] kills [(amount|count)]",
                "%offlineplayer%['s] guild['s] [(rank|ranking)] kills [(amount|count)]",
                "%location%['s] guild['s] [(rank|ranking)] kills [(amount|count)]",
                "%block%['s] guild['s] [(rank|ranking)] kills [(amount|count)]",
                "%string%['s] [guild] [(rank|ranking)] kills [(amount|count)]",
                "%object%['s] guild['s] [(rank|ranking)] kills [(amount|count)]"
            )
        }
    }

    override fun get(event: Event): Array<Int> {
        return event.getGuild(guildExpression)
            .map { guild -> guild.rank.averageKills }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0))
    }


}