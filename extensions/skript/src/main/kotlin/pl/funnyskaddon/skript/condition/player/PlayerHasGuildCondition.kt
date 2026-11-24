package pl.funnyskaddon.skript.condition.player

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.event.Event
import pl.funnyskaddon.skript.condition.PlayerCondition
import pl.funnyskaddon.skript.registration.Registration.registerCondition
import pl.funnyskaddon.skript.getUser
@Name("Has Guild")
@Since("2.0.0")
@Description("Checks if the player has a guild")
@Examples(
    "if player has guild:",
    "if player doesn′t hava guild:"
)
class PlayerHasGuildCondition : PlayerCondition() {

    companion object {
        init {
            registerCondition<PlayerHasGuildCondition>(
                "[player] %offlineplayer% has guild",
                "[player] %offlineplayer% (does not|doesn't) have guild"
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getUser(playerExpression)
            .map(User::hasGuild)
            .orElse(false)
            .xor(isNegated)
    }

}