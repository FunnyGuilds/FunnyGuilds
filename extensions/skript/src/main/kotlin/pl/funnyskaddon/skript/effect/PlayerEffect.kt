package pl.funnyskaddon.skript.effect

import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import org.bukkit.OfflinePlayer

abstract class PlayerEffect<T>(private var inverted: Boolean) : FunnyEffect() {

    protected lateinit var playerExpression: Expression<OfflinePlayer>
    protected lateinit var valueExpression: Expression<T>

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        if (inverted || matchedPattern > 1) {
            valueExpression = expression[0] as Expression<T>
            playerExpression = expression[1] as Expression<OfflinePlayer>
        } else {
            valueExpression = expression[1] as Expression<T>
            playerExpression = expression[0] as Expression<OfflinePlayer>
        }
        return true
    }

}