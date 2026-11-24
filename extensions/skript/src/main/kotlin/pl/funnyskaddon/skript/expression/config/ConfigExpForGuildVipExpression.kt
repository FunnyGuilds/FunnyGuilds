package pl.funnyskaddon.skript.expression.config

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.event.Event
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.FunnyExpression

@Name("Exp To Create Guild Vip")
@Since("2.0.0")
@Description("Returns the experience required to create a guild for players with VIP (an appropriate permission)")
@Examples(
    "send \"%required experience to create guild with vip%\"",
    "set {_exp} to required experience to create guild with vip"
)
class ConfigExpForGuildVipExpression : FunnyExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<ConfigExpForGuildVipExpression, Int>(
                PROPERTY,
                "[required] (exp|experience) (to create|for) guild [(for|with)] vip"
            )
        }
    }

    override fun get(event: Event): Array<Int> {
        return arrayOf(ConfigItemsForGuildExpression.vipItemSet().requiredLevel)
    }



}