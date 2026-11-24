package pl.funnyskaddon.skript.condition.player

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.FunnyGuilds
import org.bukkit.event.Event
import pl.funnyskaddon.skript.condition.PlayerCondition
import pl.funnyskaddon.skript.registration.Registration.registerCondition
import pl.funnyskaddon.skript.getPlayer
@Name("Is In Any Guild Region")
@Since("2.0.0")
@Description("Checks if the player is in any guild region")
@Examples(
    "if player is in any guild region:",
    "if player is not in any guild region:"
)
class PlayerIsInAnyGuildRegionCondition : PlayerCondition() {

    companion object {
        init {
            registerCondition<PlayerIsInAnyGuildRegionCondition>(
                "[player] %player% is in any guild region",
                "[player] %player% is(n't| not) in any guild region"
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getPlayer(playerExpression)
            .map { player -> FunnyGuilds.getInstance().regionManager.isInRegion(player.location) }
            .orElse(false)
            .xor(isNegated)
    }

}