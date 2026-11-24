package pl.funnyskaddon.skript.condition.player

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import pl.funnyskaddon.skript.condition.FunnyCondition
import pl.funnyskaddon.skript.getPlayer
import pl.funnyskaddon.skript.registration.Registration.registerCondition

// Wraps ProtectionSystem#isProtected with the BLOCK_PLACE permission, which is the
// same check FunnyGuilds runs in its own BlockPlace region listener (including the
// guild heart, admin bypass and "guild can't build" rules). This complements
// GuildCanBuildCondition, which only checks the guild-wide Guild#canBuild() state.
@Name("Player Can Build")
@Since("3.0.0")
@Description("Checks if a player can build (place blocks) at a location, according to FunnyGuilds region protection")
@Examples(
    "if player can build at location of targeted block:",
    "if player can't build at {_location}:"
)
class PlayerCanBuildCondition : FunnyCondition() {

    private lateinit var playerExpression: Expression<OfflinePlayer>
    private lateinit var locationExpression: Expression<Location>

    companion object {
        init {
            registerCondition<PlayerCanBuildCondition>(
                "%player% can (build|place blocks) at %location%",
                "%player% (can't|cannot|can not) (build|place blocks) at %location%"
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        playerExpression = expression[0] as Expression<OfflinePlayer>
        locationExpression = expression[1] as Expression<Location>
        isNegated = matchedPattern == 1
        return true
    }

    override fun check(event: Event): Boolean {
        val result = event.getPlayer(playerExpression)
            .map { player ->
                val location = locationExpression.getSingle(event)
                location != null && ProtectionSystem.isProtected(
                    player,
                    location,
                    null,
                    GuildProtectionPermission.BLOCK_PLACE,
                    true
                ).isPresent // present means the location is protected
            }
            .map { isProtected -> !isProtected } // not protected -> can build
            .orElse(false)

        return result xor isNegated
    }

}
