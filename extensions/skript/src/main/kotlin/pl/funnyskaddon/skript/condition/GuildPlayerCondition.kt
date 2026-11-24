package pl.funnyskaddon.skript.condition
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import org.bukkit.OfflinePlayer

abstract class GuildPlayerCondition : FunnyCondition() {

    protected lateinit var playerExpression: Expression<OfflinePlayer>
    protected lateinit var guildExpression: Expression<*>

    override fun init(
        expression: Array<Expression<*>?>,
        matchedPattern: Int,
        isDelayed: Kleenean?,
        parseResult: SkriptParser.ParseResult?
    ): Boolean {
        playerExpression = expression[0] as Expression<OfflinePlayer>
        guildExpression = expression[1] as Expression<*>
        if (parseResult != null) {
            isNegated = ((matchedPattern >= 1) xor (parseResult.mark == 1))
        }
        return true
    }

}