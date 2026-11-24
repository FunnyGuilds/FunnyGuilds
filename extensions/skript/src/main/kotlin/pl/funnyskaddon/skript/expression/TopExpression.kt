package pl.funnyskaddon.skript.expression

import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean

abstract class TopExpression<T>(returnType: Class<T>) : FunnyExpression<T>(returnType) {

    protected lateinit var positionExpression: Expression<Number>
    protected var typeExpression: Expression<String>? = null

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        positionExpression = expression[0] as Expression<Number>
        typeExpression = if (expression.size > 2) expression[1] as Expression<String> else null
        return true
    }

}