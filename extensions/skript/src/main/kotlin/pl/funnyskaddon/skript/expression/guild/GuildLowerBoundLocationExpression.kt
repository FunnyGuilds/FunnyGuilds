package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.Region
import org.bukkit.Location
import org.bukkit.event.Event
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild

@Name("Guild Lower Bound Location")
@Since("2.0.0")
@Description("Returns the lower bound of a guild's region")
@Examples(
    "send \"%\"AC4U\" guild region lower bound%\"",
    "set {_lowerBound} to \"AC4U\" guild region lower bound"
)
class GuildLowerBoundLocationExpression : GuildExpression<Location>(Location::class.java) {

    companion object {
        init {
            registerExpression<GuildLowerBoundLocationExpression, Location>(
                PROPERTY,
                "%object%['s] guild['s] [region] (lower|down) (bound|point) [location]",
                "%offlineplayer%['s] guild['s] [region] (lower|down) (bound|point) [location]",
                "%guild%['s] [region] (lower|down) (bound|point) [location]",
                "%block%['s] guild['s] [region] (lower|down) (bound|point) [location]",
                "%location%['s] guild['s] [region] (lower|down) (bound|point) [location]",
                "%string%['s] [guild] [region] (lower|down) (bound|point) [location]"
            )
        }
    }

    override fun get(event: Event): Array<Location>? {
        return event.getGuild(guildExpression)
            .flatMap(Guild::getRegion)
            .map(Region::getLowerCorner)
            .map { value -> arrayOf(value) }
            .orNull()
    }


}