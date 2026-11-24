package pl.funnyskaddon.skript.expression.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import net.dzikoysk.funnyguilds.rank.DefaultTops
import org.bukkit.event.Event
import pl.funnyskaddon.skript.registration.Registration.guildSources
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getValue

@Name("Guild Top Position")
@Since("2.0.0")
@Description("Returns the position of a guild in the ranking")
@Examples(
    "send \"%player's guild top position%\"",
    "set {_position} to player's guild top position"
)
class GuildPositionExpression : GuildExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<GuildPositionExpression, Int>(
                PROPERTY,
                *guildSources("['s] [(rank|ranking)] position [of %-string%]")
            )
        }
    }

    protected var typeExpression: Expression<String>? = null

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        typeExpression = if (expression.size > 2) expression[1] as Expression<String> else null
        return super.init(expression, matchedPattern, isDelayed, parseResult)
    }

    override fun get(event: Event): Array<Int> {
        return event.getGuild(guildExpression)
            .map { guild ->
                val type: String = event.getValue(typeExpression).orElse(DefaultTops.GUILD_AVG_POINTS_TOP)
                guild.rank.getPosition(type)
            }
            .map { value -> arrayOf(value) }.orElse(arrayOf(0))
    }


    override fun toString(e: Event?, debug: Boolean): String {
        if (e != null) {
            val type: String = e.getValue(typeExpression).orElse(DefaultTops.GUILD_AVG_POINTS_TOP)
            return "raking position of guild \"${e.getGuild(guildExpression)}\" in $type top"
        }
        return "ranking position of guild"
    }

}