package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.flatMapOption
import pl.funnyskaddon.extension.toStream

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.entity.Player
import org.bukkit.event.Event
import pl.funnyskaddon.extension.getPlayerOption
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild
import java.util.stream.Collectors

@Name("Guild Online Deputies")
@Since("2.0.0")
@Description("Returns the deputies of a guild that are online")
@Examples(
    "loop player's guild online deputies:",
    "    send \"%loop-value%\""
)
class GuildOnlineDeputiesExpression : GuildExpression<Player>(Player::class.java) {

    companion object {
        init {
            registerExpression<GuildOnlineDeputiesExpression, Player>(
                PROPERTY,
                "%object%['s] [guild] online deputies",
                "%guild%['s] online deputies",
                "%string%['s] [guild] online deputies",
                "%offlineplayer%['s] guild['s] online deputies",
                "%location%['s] guild['s] online deputies",
                "%block%['s] guild['s] online deputies",
                "%object%['s] guild['s] online deputies"
            )
        }
    }

    override fun get(event: Event): Array<Player> {
        return event.getGuild(guildExpression).toStream()
            .flatMap { it.getOnlineMembers(false).stream() }
            .filter(User::isDeputy)
            .flatMapOption(User::getPlayerOption)
            .collect(Collectors.toSet())
            .toTypedArray()
    }

    override fun isSingle(): Boolean {
        return false
    }


}