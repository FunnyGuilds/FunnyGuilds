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

@Name("Guild Lives")
@Since("2.0.0")
@Description("Returns the number of lives of a guild")
@Examples(
    "send \"%\"\"FajnaGildia\"\" guild lives%\"",
    "set {_lives} to \"FajnaGildia\" guild lives"
)
class GuildLivesExpression : GuildExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<GuildLivesExpression, Int>(
                PROPERTY,
                "%object%['s] guild['s] lives [(amount|count)]",
                "%object%['s] [guild] lives [(amount|count)]",
                "%location%['s] guild['s] lives [(amount|count)]",
                "%offlineplayer%['s] guild['s] lives [(amount|count)]",
                "%block%['s] guild['s] lives [(amount|count)]",
                "%guild%['s] lives [(amount|count)]",
                "%string%['s] [guild] lives [(amount|count)]"
            )
        }
    }

    override fun get(event: Event): Array<Int>? {
        return event.getGuild(guildExpression)
            .map { guild -> guild.lives }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0))
    }


}