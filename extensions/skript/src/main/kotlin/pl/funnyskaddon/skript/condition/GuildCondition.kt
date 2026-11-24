package pl.funnyskaddon.skript.condition
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean

abstract class GuildCondition(private val negatedIndex: Int = 1) : FunnyCondition() {

    lateinit var guildExpression: Expression<*>

    override fun init(
        expression: Array<Expression<*>?>,
        matchedPattern: Int,
        isDelayed: Kleenean?,
        parseResult: SkriptParser.ParseResult?
    ): Boolean {
        guildExpression = expression[0] as Expression<*>
        if (parseResult != null) {
            isNegated = ((matchedPattern >= negatedIndex) xor (parseResult.mark == 1))
        }
        return true
    }

}