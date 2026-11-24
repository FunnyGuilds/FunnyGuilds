package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.skript.registration.Registration.guildSources
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.flatMapOption
import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import pl.funnyskaddon.extension.getOfflinePlayer
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild

@Name("Guild Owner")
@Since("2.0.0")
@Description("Returns the owner of a guild")
@Examples(
    "send \"%player's guild owner%\"",
    "set {_owner} to player's guild owner"
)
class GuildOwnerExpression : GuildExpression<OfflinePlayer>(OfflinePlayer::class.java) {

    companion object {
        init {
            registerExpression<GuildOwnerExpression, OfflinePlayer>(
                PROPERTY,
                *guildSources("['s] (owner|leader)")
            )
        }
    }

    override fun get(event: Event): Array<OfflinePlayer>? {
        return event.getGuild(guildExpression)
            .map(Guild::getOwner)
            .flatMap(User::getOfflinePlayer)
            .map { value -> arrayOf(value) }
            .orNull()
    }


}