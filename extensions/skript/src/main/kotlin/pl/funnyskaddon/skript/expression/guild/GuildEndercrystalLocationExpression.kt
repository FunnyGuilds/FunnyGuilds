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

@Name("Guild Endercrystal Location")
@Since("2.0.0")
@Description("Returns the location of a guild (its crystal/heart)")
@Examples(
    "send \"%\"\"AC4U\"\" guild endercrystal location%\"",
    "set {_enderCrystal} to \"AC4U\" guild endercrystal location"
)
class GuildEndercrystalLocationExpression : GuildExpression<Location>(Location::class.java) {

    companion object {
        init {
            registerExpression<GuildEndercrystalLocationExpression, Location>(
                PROPERTY,
                "%object%['s] guild['s] (endercrystal|core) [location]",
                "%string%['s] [guild] (endercrystal|core) [location]",
                "%offlineplayer%['s] guild['s] (endercrystal|core) [location]",
                "%location%['s] guild['s] (endercrystal|core) [location]",
                "%guild%['s] (endercrystal|core) [location]",
                "%block%['s] guild['s] (endercrystal|core) [location]"
            )
        }
    }

    override fun get(event: Event): Array<Location>? {
        return event.getGuild(guildExpression)
            .flatMap(Guild::getEnderCrystal)
            .map { value -> arrayOf(value) }
            .orNull()
    }


}