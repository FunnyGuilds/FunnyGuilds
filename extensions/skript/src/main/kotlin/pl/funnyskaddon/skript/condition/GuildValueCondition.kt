package pl.funnyskaddon.skript.condition
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean

abstract class GuildValueCondition<T>(private var inverted: Boolean, invertedIndex: Int = 2) :
    GuildCondition(invertedIndex) {

    protected lateinit var valueExpression: Expression<T>

    override fun init(
        expression: Array<Expression<*>?>,
        matchedPattern: Int,
        isDelayed: Kleenean?,
        parseResult: SkriptParser.ParseResult?
    ): Boolean {
        if (inverted) {
            valueExpression = expression[0] as Expression<T>
            guildExpression = expression[1] as Expression<*>
        } else {
            valueExpression = expression[1] as Expression<T>
            guildExpression = expression[0] as Expression<*>
        }
        return true
    }

}