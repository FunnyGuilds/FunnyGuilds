package pl.funnyskaddon.skript.condition.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import org.bukkit.event.Event
import pl.funnyskaddon.skript.condition.FunnyCondition
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.registration.Registration.registerCondition

// Uses Guild#hasAllyPvPEnabled(ally), which is true when both guilds are allies
// and this guild has PvP with that ally toggled on. The check requires the toggle
// in both directions, since PvP between two guilds needs both to allow it.
@Name("Guild Allies PvP Is Enabled")
@Since("3.0.0")
@Description("Checks if PvP between two allied guilds is enabled")
@Examples(
    "if guild \"AC4U\" allies pvp with guild \"AC4X\" is enabled:",
    "if guild \"AC4U\" allies pvp with guild \"AC4X\" is disabled:"
)
class GuildAllyPvPIsEnabledCondition : FunnyCondition() {

    private lateinit var firstGuildExpression: Expression<*>
    private lateinit var secondGuildExpression: Expression<*>

    companion object {
        init {
            registerCondition<GuildAllyPvPIsEnabledCondition>(
                "%guild%['s] (allies|ally) pvp with %guild% is (enabled|on)",
                "%guild%['s] (allies|ally) pvp with %guild% is (disabled|off)"
            )
        }
    }

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        firstGuildExpression = expression[0]
        secondGuildExpression = expression[1]
        isNegated = matchedPattern == 1
        return true
    }

    override fun check(event: Event): Boolean {
        val firstGuild = event.getGuild(firstGuildExpression)
        val secondGuild = event.getGuild(secondGuildExpression)

        val result = firstGuild.isPresent && secondGuild.isPresent &&
            firstGuild.get().hasAllyPvPEnabled(secondGuild.get()) &&
            secondGuild.get().hasAllyPvPEnabled(firstGuild.get())

        return result xor isNegated
    }

}
