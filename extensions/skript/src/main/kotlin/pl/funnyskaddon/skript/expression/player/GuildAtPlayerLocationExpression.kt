package pl.funnyskaddon.skript.expression.player

import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.Region
import org.bukkit.entity.Player
import org.bukkit.event.Event
import pl.funnyskaddon.skript.expression.PlayerExpression
import pl.funnyskaddon.skript.getPlayer

@Name("Guild At Player Location")
@Since("2.0.0")
@Description("Returns the guild at the player's location (the guild whose region the player is in)")
@Examples(
    "send \"%guild at player's location%\"",
    "set {_guild} to guild at player's location"
)
class GuildAtPlayerLocationExpression : PlayerExpression<Guild>(Guild::class.java) {

    companion object {
        init {
            registerExpression<GuildAtPlayerLocationExpression, Guild>(
                PROPERTY,
                "guild at %offlineplayer%['s] location"
            )
        }
    }

    override fun get(event: Event): Array<Guild>? {
        return event.getPlayer(playerExpression)
            .map(Player::getLocation)
            .flatMap(FunnyGuilds.getInstance().regionManager::findRegionAtLocation)
            .map(Region::getGuild)
            .map { value -> arrayOf(value) }
            .orNull()
    }


    override fun toString(e: Event?, debug: Boolean): String {
        if (e != null) {
            return "guild at ${playerExpression.toString(e, debug)}'s location"
        }
        return "guild at player's location"
    }

}