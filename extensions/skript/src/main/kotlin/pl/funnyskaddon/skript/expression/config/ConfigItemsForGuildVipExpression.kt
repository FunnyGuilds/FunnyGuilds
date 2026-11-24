package pl.funnyskaddon.skript.expression.config

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.FunnyExpression

@Name("Items To Create Guild (VIP)")
@Since("2.0.0")
@Description("Returns the items required to create a guild for a player with VIP")
@Examples(
    "send \"%required items to create guild vip%\"",
    "set {_items} to required items to create guild vip"
)
class ConfigItemsForGuildVipExpression : FunnyExpression<ItemStack>(ItemStack::class.java) {

    companion object {
        init {
            registerExpression<ConfigItemsForGuildVipExpression, ItemStack>(
                PROPERTY,
                "[required] items (to create|for) guild [with] vip"
            )
        }
    }

    override fun get(event: Event): Array<ItemStack> {
        val itemsConfig = net.dzikoysk.funnyguilds.FunnyGuilds.getInstance().itemsConfiguration
        return ItemUtils.buildRequiredItems(ConfigItemsForGuildExpression.vipItemSet(), itemsConfig).toTypedArray()
    }

    override fun isSingle(): Boolean {
        return false
    }



}
