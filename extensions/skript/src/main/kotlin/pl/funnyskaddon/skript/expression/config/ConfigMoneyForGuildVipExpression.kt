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
@Description("Returns the money required to create a guild for players with VIP (an appropriate permission)")
@Examples(
    "send \"%required money to create guild with vip%\"",
    "set {_money} to required money to create guild with vip"
)
class ConfigMoneyForGuildVipExpression : FunnyExpression<Double>(Double::class.javaObjectType) {

    companion object {
        init {
            registerExpression<ConfigMoneyForGuildVipExpression, Double>(
                PROPERTY,
                "[required] money (to create|for) guild [(for|with)] vip"
            )
        }
    }

    override fun get(event: Event): Array<Double> {
        return arrayOf(ConfigItemsForGuildExpression.vipItemSet().requiredMoney)
    }



}