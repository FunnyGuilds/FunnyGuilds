package pl.funnyskaddon.skript.expression

import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.doc.Name
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import org.bukkit.event.Event

abstract class FunnyExpression<T>(private val returnType: Class<T>) : SimpleExpression<T>() {

    override fun getReturnType(): Class<T> {
        return returnType
    }

    override fun toString(event: Event?, debug: Boolean): String =
        javaClass.getAnnotation(Name::class.java)?.value?.lowercase() ?: javaClass.simpleName

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        return true
    }

    override fun isSingle(): Boolean {
        return true
    }

}