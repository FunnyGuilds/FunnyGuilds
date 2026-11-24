package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.extension.toStream

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
import java.util.stream.Collectors

@Name("Guild Allies")
@Since("2.0.0")
@Description("Returns the allies of a guild")
@Examples(
    "loop \"AC4U\" guild allies:",
    "    send \"%loop-value%\""
)
class GuildAlliesExpression : GuildExpression<Guild>(Guild::class.java) {

    companion object {
        init {
            registerExpression<GuildAlliesExpression, Guild>(
                PROPERTY,
                "%object%['s] guild['s] allies",
                "%block%['s] guild['s] allies",
                "%offlineplayer%['s] guild['s] allies",
                "%guild%['s] allies",
                "%string%['s] [guild] allies",
                "%location%['s] guild['s] allies"
            )
        }
    }

    override fun get(event: Event): Array<Guild> {
        return event.getGuild(guildExpression).toStream()
            .flatMap { it.allies.stream() }
            .collect(Collectors.toSet())
            .toTypedArray()
    }

    override fun isSingle(): Boolean {
        return false
    }


}