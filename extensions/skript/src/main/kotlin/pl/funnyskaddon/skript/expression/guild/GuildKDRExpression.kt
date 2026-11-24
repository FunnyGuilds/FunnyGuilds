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

@Name("Guild KDR")
@Since("2.0.0")
@Description("Returns the KDR of a guild")
@Examples(
    "send \"%\"\"FajnaGildia\"\" guild kdr%\"",
    "set {_kdr} to \"FajnGildia\" guild kdr"
)
class GuildKDRExpression : GuildExpression<Float>(Float::class.javaObjectType) {

    companion object {
        init {
            registerExpression<GuildKDRExpression, Float>(
                PROPERTY,
                "%offlineplayer%['s] guild['s] [(rank|ranking)] kdr",
                "%guild%['s] [(rank|ranking)] kdr",
                "%block%['s] guild['s] [(rank|ranking)] kdr",
                "%object%['s] guild['s] [(rank|ranking)] kdr",
                "%string%['s] [guild] [(rank|ranking)] kdr",
                "%location%['s] guild['s] [(rank|ranking)] kdr"
            )
        }
    }

    override fun get(event: Event): Array<Float> {
        return event.getGuild(guildExpression)
            .map { guild -> guild.rank.kdr }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0F))
    }


}