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

@Name("Items To Extension Validity")
@Since("2.0.0")
@Description("Returns the items required to extend a guild's validity")
@Examples(
    "send \"%required items to extension validity%\"",
    "set {_items} to extension validity"
)
class ConfigItemsToExtensionValidityGuildExpression : FunnyExpression<ItemStack>(ItemStack::class.java) {

    companion object {
        init {
            registerExpression<ConfigItemsToExtensionValidityGuildExpression, ItemStack>(
                PROPERTY,
                "[required] items (to|for) extension [guild] validity"
            )
        }
    }

    override fun get(event: Event): Array<ItemStack> {
        return FunnySkAddon.fgConfiguration.validityItems.toTypedArray()
    }

    override fun isSingle(): Boolean {
        return false
    }



}