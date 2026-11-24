package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.event.Event
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.ValueExpression
import pl.funnyskaddon.skript.getValue

@Name("Guild From Tag")
@Since("2.0.0")
@Description("Returns a guild by its tag")
@Examples("set {_guild} to guild with tag \"AC4U\"")
class GuildFromTagExpression : ValueExpression<String>() {

    companion object {
        init {
            registerExpression<GuildFromTagExpression, Guild>(
                PROPERTY,
                "guild with tag %string%",
                "guild from tag %string%"
            )
        }
    }

    override fun get(event: Event): Array<Guild>? {
        return event.getValue(valueExpression)
            .flatMap(FunnyGuilds.getInstance().guildManager::findByTag)
            .map { value -> arrayOf(value) }
            .orNull()
    }

    override fun toString(e: Event?, debug: Boolean): String {
        return "guild from tag " + valueExpression.toString(e, debug)
    }

}