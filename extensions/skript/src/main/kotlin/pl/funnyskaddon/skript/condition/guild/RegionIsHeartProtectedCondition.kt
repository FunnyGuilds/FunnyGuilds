package pl.funnyskaddon.skript.condition.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem
import org.bukkit.Location
import org.bukkit.event.Event
import pl.funnyskaddon.skript.condition.FunnyCondition
import pl.funnyskaddon.skript.registration.Registration.registerCondition

// FunnyGuilds 5.x has no single "region protection" flag; protection is driven by
// the interaction-protection settings of the guild heart (HeartConfiguration) and
// checked via ProtectionSystem#isGuildHeartProtectedRegion(Location). This condition
// exposes that check: whether the location is inside a protected guild heart region.
@Name("Region Is Heart Protected")
@Since("3.0.0")
@Description("Checks if a location is in a protected guild heart region")
@Examples(
    "if region at location of player is protected:",
    "if region at {_location} is not protected:"
)
class RegionIsHeartProtectedCondition : FunnyCondition() {

    private lateinit var locationExpression: Expression<Location>

    companion object {
        init {
            registerCondition<RegionIsHeartProtectedCondition>(
                "region at %location% is protected",
                "region at %location% is(n't| not) protected"
            )
        }
    }

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        locationExpression = expression[0] as Expression<Location>
        isNegated = matchedPattern == 1
        return true
    }

    override fun check(event: Event): Boolean {
        val location = locationExpression.getSingle(event) ?: return isNegated
        return ProtectionSystem.isGuildHeartProtectedRegion(location) xor isNegated
    }

}
