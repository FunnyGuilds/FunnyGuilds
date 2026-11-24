package pl.funnyskaddon.skript.condition.player

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.event.Event
import pl.funnyskaddon.skript.condition.GuildPlayerCondition
import pl.funnyskaddon.skript.registration.Registration.registerCondition
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getUser
@Name("Is In Guild")
@Since("2.0.0")
@Description("Checks if the player is a member of the given guild")
@Examples(
    "if player is in guild \"AC4U\":",
    "if player is not in guild \"AC4U\":"
)
class PlayerIsInGuildCondition : GuildPlayerCondition() {

    companion object {
        init {
            registerCondition<PlayerIsInGuildCondition>(
                "[player] %offlineplayer% is in guild %object%",
                "[player] %offlineplayer% is(n't| not) in guild %object%"
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getUser(playerExpression)
            .map(User::getGuild)
            .map { guild -> guild.equals(event.getGuild(guildExpression)) }
            .orElse(false)
            .xor(isNegated)
    }

}