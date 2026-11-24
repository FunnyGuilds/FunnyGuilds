package pl.funnyskaddon.skript.expression

import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean

abstract class GuildExpression<T>(returnType: Class<T>) : FunnyExpression<T>(returnType) {

    protected lateinit var guildExpression: Expression<*>

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        guildExpression = expression[0]
        return true
    }

}
