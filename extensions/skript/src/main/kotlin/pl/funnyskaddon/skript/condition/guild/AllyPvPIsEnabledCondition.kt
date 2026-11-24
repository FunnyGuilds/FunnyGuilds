package pl.funnyskaddon.skript.condition.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import net.dzikoysk.funnyguilds.FunnyGuilds
import org.bukkit.event.Event
import pl.funnyskaddon.skript.condition.FunnyCondition
import pl.funnyskaddon.skript.registration.Registration.registerCondition

// Reads the global FunnyGuilds config option "damage-ally" (whether players can
// damage their guild's allies). Per-guild-pair ally PvP state is covered by
// GuildAllyPvPIsEnabledCondition.
@Name("Allies PvP Is Enabled")
@Since("3.0.0")
@Description("Checks if PvP between allies is enabled in the FunnyGuilds config")
@Examples(
    "if allies pvp is enabled:",
    "if allies pvp is disabled:"
)
class AllyPvPIsEnabledCondition : FunnyCondition() {

    companion object {
        init {
            registerCondition<AllyPvPIsEnabledCondition>(
                "allies pvp is (enabled|on)",
                "allies pvp is (disabled|off)"
            )
        }
    }

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        isNegated = matchedPattern == 1
        return true
    }

    override fun check(event: Event): Boolean {
        return FunnyGuilds.getInstance().pluginConfiguration.damageAlly xor isNegated
    }

}
