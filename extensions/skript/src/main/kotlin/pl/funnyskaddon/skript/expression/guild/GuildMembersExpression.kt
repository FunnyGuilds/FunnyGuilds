package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.extension.flatMapOption
import pl.funnyskaddon.extension.toStream

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import pl.funnyskaddon.extension.getOfflinePlayer
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild
import java.util.stream.Collectors

@Name("Guild Members")
@Since("2.0.0")
@Description("Returns the members of a guild")
@Examples(
    "loop player's guild members:",
    "    send \"%loop-value%\""
)
class GuildMembersExpression : GuildExpression<OfflinePlayer>(OfflinePlayer::class.java) {

    companion object {
        init {
            registerExpression<GuildMembersExpression, OfflinePlayer>(
                PROPERTY,
                "%string%['s] [guild] members",
                "%guild%['s] members",
                "%block%['s] guild['s] members",
                "%location%['s] guild['s] members",
                "%object%['s] guild['s] members",
                "%offlineplayer%['s] guild['s] members"
            )
        }
    }

    override fun get(event: Event): Array<OfflinePlayer> {
        return event.getGuild(guildExpression).toStream()
            .flatMap { it.members.stream() }
            .flatMapOption(User::getOfflinePlayer)
            .collect(Collectors.toSet())
            .toTypedArray()
    }

    override fun isSingle(): Boolean {
        return false
    }


}