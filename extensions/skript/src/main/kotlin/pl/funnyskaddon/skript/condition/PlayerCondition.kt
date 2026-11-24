package pl.funnyskaddon.skript.condition
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import org.bukkit.OfflinePlayer

abstract class PlayerCondition : FunnyCondition() {

    protected lateinit var playerExpression: Expression<OfflinePlayer>

    override fun init(
        expression: Array<Expression<*>?>,
        matchedPattern: Int,
        isDelayed: Kleenean?,
        parseResult: SkriptParser.ParseResult?
    ): Boolean {
        playerExpression = expression[0] as Expression<OfflinePlayer>
        if (parseResult != null) {
            isNegated = ((matchedPattern >= 1) xor (parseResult.mark == 1))
        }
        return true
    }

}