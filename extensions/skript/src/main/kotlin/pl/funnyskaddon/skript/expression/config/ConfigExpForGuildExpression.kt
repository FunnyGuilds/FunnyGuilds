package pl.funnyskaddon.skript.expression.config

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.event.Event
import pl.funnyskaddon.FunnySkAddon
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.FunnyExpression

@Name("Exp To Create Guild")
@Since("2.0.0")
@Description("Returns the level required to create a guild")
@Examples(
    "send \"%required experience to create guild%\"",
    "set {_exp} to required experience to create guild"
)
class ConfigExpForGuildExpression : FunnyExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<ConfigExpForGuildExpression, Int>(
                PROPERTY,
                "[required] (exp|experience) (to create|for) guild"
            )
        }
    }

    override fun get(event: Event): Array<Int> {
        return arrayOf(ConfigItemsForGuildExpression.defaultItemSet().requiredLevel)
    }



}