package pl.funnyskaddon.skript.expression.config

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.event.Event
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.FunnyExpression

@Name("Money To Create Guild")
@Since("2.0.0")
@Description("Returns the money required to create a guild")
@Examples(
    "send \"%required money to create guild%\"",
    "set {_money} to required money to create guild"
)
class ConfigMoneyForGuildExpression : FunnyExpression<Double>(Double::class.javaObjectType) {

    companion object {
        init {
            registerExpression<ConfigMoneyForGuildExpression, Double>(
                PROPERTY,
                "[required] money (to create|for) guild"
            )
        }
    }

    override fun get(event: Event): Array<Double> {
        return arrayOf(ConfigItemsForGuildExpression.defaultItemSet().requiredMoney)
    }



}