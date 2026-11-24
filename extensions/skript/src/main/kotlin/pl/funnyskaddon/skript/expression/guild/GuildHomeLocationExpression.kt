package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.Location
import org.bukkit.event.Event
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild

@Name("Guild Home Location")
@Since("2.0.0")
@Description("Returns the location of a guild's home")
@Examples(
    "send \"%\"AC4U\" guild home location%\"",
    "set {_home} to \"AC4U\" guild home location"
)
class GuildHomeLocationExpression : GuildExpression<Location>(Location::class.java) {

    companion object {
        init {
            registerExpression<GuildHomeLocationExpression, Location>(
                PROPERTY,
                "%offlineplayer%['s] guild['s] (home|base) [location]",
                "%location%['s] guild['s] (home|base) [location]",
                "%object%['s] guild['s] (home|base) [location]",
                "%block%['s] guild['s] (home|base) [location]",
                "%guild%['s] (home|base) [location]",
                "%string%['s] [guild] (home|base) [location]"
            )
        }
    }

    override fun get(event: Event): Array<Location>? {
        return event.getGuild(guildExpression)
            .flatMap(Guild::getHome)
            .map { value -> arrayOf(value) }
            .orNull()
    }


}