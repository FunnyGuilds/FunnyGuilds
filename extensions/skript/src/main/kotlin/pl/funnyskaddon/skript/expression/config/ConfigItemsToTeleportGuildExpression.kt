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

@Name("Items To Teleport Guild")
@Since("2.0.0")
@Description("Returns the items required to teleport to a guild")
@Examples(
    "send \"%required items to teleport guild base%\"",
    "set {_items} to required items to teleport guild bas"
)
class ConfigItemsToTeleportGuildExpression : FunnyExpression<ItemStack>(ItemStack::class.java) {

    companion object {
        init {
            registerExpression<ConfigItemsToTeleportGuildExpression, ItemStack>(
                PROPERTY,
                "[required] items (to|for) teleport guild[( |-)(home|base)]"
            )
        }
    }

    override fun get(event: Event): Array<ItemStack> {
        return FunnySkAddon.fgConfiguration.baseItems.toTypedArray()
    }

    override fun isSingle(): Boolean {
        return false
    }



}