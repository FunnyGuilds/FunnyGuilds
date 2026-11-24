package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.skript.registration.Registration.guildSources
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.event.Event
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild

@Name("Guild Tag")
@Since("2.0.0")
@Description("Returns the tag of a guild")
@Examples(
    "send \"%player's guild tag%\"",
    "set {_tag} to player's guild tag"
)
class GuildTagExpression : GuildExpression<String>(String::class.java) {

    companion object {
        init {
            registerExpression<GuildTagExpression, String>(
                PROPERTY,
                *guildSources("['s] tag")
            )
        }
    }

    override fun get(event: Event): Array<String>? {
        return event.getGuild(guildExpression)
            .map(Guild::getTag)
            .map { value -> arrayOf(value) }
            .orNull()
    }


}