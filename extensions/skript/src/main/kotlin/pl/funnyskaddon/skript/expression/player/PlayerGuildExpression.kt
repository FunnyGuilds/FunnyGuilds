package pl.funnyskaddon.skript.expression.player

import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.event.Event
import pl.funnyskaddon.skript.expression.PlayerExpression
import pl.funnyskaddon.skript.getUser

@Name("Player Guild")
@Since("2.0.0")
@Description("Returns the guild of a player")
@Examples(
    "send \"%player guild%\"",
    "set {_guild} to player's guild"
)
class PlayerGuildExpression : PlayerExpression<Guild>(Guild::class.java) {

    companion object {
        init {
            registerExpression<PlayerGuildExpression, Guild>(
                PROPERTY,
                "%offlineplayer%['s] guild"
            )
        }
    }

    override fun get(event: Event): Array<Guild>? {
        return event.getUser(playerExpression)
            .flatMap(User::getGuild)
            .map { value -> arrayOf(value) }
            .orNull()
    }


}