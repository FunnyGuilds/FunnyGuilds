package pl.funnyskaddon.skript.condition.player

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.event.Event
import pl.funnyskaddon.extension.isPlayerInGuildRegion
import pl.funnyskaddon.skript.condition.GuildPlayerCondition
import pl.funnyskaddon.skript.registration.Registration.registerCondition
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getPlayer
@Name("Is In Guild Region")
@Since("2.0.0")
@Description("Checks if the player is in the guild's region")
@Examples(
    "if player is in guild \"AC4U\" region:",
    "if player is not in guild \"AC4U\" region:"
)
class PlayerIsInGuildRegionCondition : GuildPlayerCondition() {

    companion object {
        init {
            registerCondition<PlayerIsInGuildRegionCondition>(
                "[player] %player% is in guild %object% region",
                "[player] %player% is(n't| not) in guild %object% region"
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getPlayer(playerExpression)
            .map { player ->
                event.getGuild(guildExpression).map { guild -> guild.isPlayerInGuildRegion(player) }
                    .orElse(false)
            }
            .orElse(false)
            .xor(isNegated)
    }

}