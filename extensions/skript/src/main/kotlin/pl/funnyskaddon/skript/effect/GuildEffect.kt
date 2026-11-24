package pl.funnyskaddon.skript.effect

import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean

abstract class GuildEffect : FunnyEffect() {

    protected lateinit var guildExpression: Expression<Any>

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        guildExpression = expression[0] as Expression<Any>
        return true
    }

}