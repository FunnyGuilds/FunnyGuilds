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
@Name("Is Owner")
@Since("2.0.0")
@Description("Checks if the player is the guild owner")
@Examples(
    "if player is owner:",
    "if player is not owner:"
)
class PlayerIsOwnerCondition : PlayerCondition() {

    companion object {
        init {
            registerCondition<PlayerIsOwnerCondition>(
                "[player] %offlineplayer% is [guild( |-)]owner",
                "[player] %offlineplayer% is(n't| not) [guild( |-)]owner"
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getUser(playerExpression)
            .map(User::isOwner)
            .orElse(false)
            .xor(isNegated)
    }

}