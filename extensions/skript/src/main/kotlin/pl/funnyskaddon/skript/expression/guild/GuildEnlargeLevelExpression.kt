package pl.funnyskaddon.skript.expression.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.event.Event
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild

@Name("Guild Enlarge Level")
@Since("2.0.0")
@Description("Returns the enlarge level of a guild")
@Examples(
    "send \"%\"AC4U\" guild enlarge level%\"",
    "set {_enlarge} to \"AC4U\" guild enlarge level"
)
class GuildEnlargeLevelExpression : GuildExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<GuildEnlargeLevelExpression, Int>(
                PROPERTY,
                "%string%['s] [guild] [region] enlarge (level|lvl)",
                "%object%['s] guild['s] [region] enlarge (level|lvl)",
                "%offlineplayer%['s] guild['s] [region] enlarge (level|lvl)",
                "%guild%['s] [region] enlarge (level|lvl)",
                "%block%['s] guild['s] [region] enlarge (level|lvl)",
                "%location%['s] guild['s] [region] enlarge (level|lvl)"
            )
        }
    }

    override fun get(event: Event): Array<Int>? {
        return event.getGuild(guildExpression)
            .flatMap(Guild::getRegion)
            .map { region -> region.enlargementLevel }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0))
    }


}