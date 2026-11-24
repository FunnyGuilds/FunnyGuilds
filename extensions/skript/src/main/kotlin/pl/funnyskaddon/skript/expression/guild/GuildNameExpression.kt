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

@Name("Guild Name")
@Since("2.0.0")
@Description("Returns the name of a guild")
@Examples(
    "send \"%player's guild name%\"",
    "set {_name} to player's guild name"
)
class GuildNameExpression : GuildExpression<String>(String::class.java) {

    companion object {
        init {
            registerExpression<GuildNameExpression, String>(
                PROPERTY,
                "%string%['s] [guild] name",
                "%location%['s] guild['s] name",
                "%offlineplayer%['s] guild['s] name",
                "%object%['s] guild['s] name",
                "%block%['s] guild['s] name",
                "%guild%['s] name"
            )
        }
    }

    override fun get(event: Event): Array<String>? {
        return event.getGuild(guildExpression)
            .map(Guild::getName)
            .map { value -> arrayOf(value) }
            .orNull()
    }


}