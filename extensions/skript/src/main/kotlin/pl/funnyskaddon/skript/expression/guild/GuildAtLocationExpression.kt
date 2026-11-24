package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.Region
import org.bukkit.Location
import org.bukkit.event.Event
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.ValueExpression
import pl.funnyskaddon.skript.getValue

@Name("Guild At Location")
@Since("2.0.0")
@Description("Returns the guild at the given location")
@Examples("set {_guild} to guild at player's location")
class GuildAtLocationExpression : ValueExpression<Location>() {

    companion object {
        init {
            registerExpression<GuildAtLocationExpression, Guild>(
                PROPERTY,
                "guild at [location] %location%"
            )
        }
    }

    override fun get(event: Event): Array<Guild>? {
        return event.getValue(valueExpression)
            .flatMap(FunnyGuilds.getInstance().regionManager::findRegionAtLocation)
            .map(Region::getGuild)
            .map { value -> arrayOf(value) }
            .orNull()
    }

    override fun toString(e: Event?, debug: Boolean): String {
        return "guild at location" + valueExpression.toString(e, debug)
    }

}