package pl.funnyskaddon.skript.expression.config

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import pl.funnyskaddon.FunnySkAddon
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.FunnyExpression

@Name("Items To Join Guild")
@Since("2.0.0")
@Description("Returns the items required to join a guild")
@Examples(
    "send \"%required items to join guild%\"",
    "set {_items} to required items to join guild"
)
class ConfigItemsToJoinGuildExpression : FunnyExpression<ItemStack>(ItemStack::class.java) {

    companion object {
        init {
            registerExpression<ConfigItemsToJoinGuildExpression, ItemStack>(
                PROPERTY,
                "[required] items (to|for) join guild"
            )
        }
    }

    override fun get(event: Event): Array<ItemStack> {
        return FunnySkAddon.fgConfiguration.joinItems.toTypedArray()
    }

    override fun isSingle(): Boolean {
        return false
    }



}