package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.extension.orNull

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
import kotlin.math.abs

@Name("Guild Area")
@Since("2.0.0")
@Description("Returns the area of a guild's region")
@Examples(
    "send \"%\"\"AC4U\"\" guild region area%\"",
    "set {_area} to \"AC4U\" guild region area"
)
class GuildAreaExpression : GuildExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<GuildAreaExpression, Int>(
                PROPERTY,
                "%string%['s] [guild] [region] area",
                "%object%['s] guild['s] [region] area",
                "%offlineplayer%['s] guild['s] [region] area",
                "%guild%['s] [region] area",
                "%block%['s] guild['s] [region] area",
                "%location%['s] guild['s] [region] area"
            )
        }
    }

    override fun get(event: Event): Array<Int>? {
        return event.getGuild(guildExpression)
            .flatMap(Guild::getRegion)
            .map { region -> abs((region.lowerX - region.upperX) * (region.lowerZ - region.upperZ)) }
            .map { value -> arrayOf(value) }
            .orNull()
    }


}