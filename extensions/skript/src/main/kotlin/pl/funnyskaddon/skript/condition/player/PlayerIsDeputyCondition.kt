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
@Name("Is Deputy")
@Since("2.0.0")
@Description("Checks if the player is a guild deputy")
@Examples(
    "if player is deputy:",
    "if player is not deputy:"
)
class PlayerIsDeputyCondition : PlayerCondition() {

    companion object {
        init {
            registerCondition<PlayerIsDeputyCondition>(
                "[player] %offlineplayer% is deputy",
                "[player] %offlineplayer% is(n't| not) deputy"
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getUser(playerExpression)
            .map(User::isDeputy)
            .orElse(false)
            .xor(isNegated)
    }

}