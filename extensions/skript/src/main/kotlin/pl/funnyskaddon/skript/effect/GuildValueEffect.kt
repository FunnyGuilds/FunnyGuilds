package pl.funnyskaddon.skript.effect

import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean

abstract class GuildValueEffect<T>(private var inverted: Boolean) : GuildEffect() {

    protected lateinit var valueExpression: Expression<T>

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        if (inverted || matchedPattern > 1) {
            valueExpression = expression[0] as Expression<T>
            guildExpression = expression[1] as Expression<Any>
        } else {
            valueExpression = expression[1] as Expression<T>
            guildExpression = expression[0] as Expression<Any>
        }
        return true
    }

}