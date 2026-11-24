package pl.funnyskaddon.skript.expression.config

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.FunnyExpression

@Name("Items To Create Guild")
@Since("2.0.0")
@Description("Returns the items required to create a guild")
@Examples(
    "send \"%required items to create guild%\"",
    "set {_items} to required items to create guild"
)
class ConfigItemsForGuildExpression : FunnyExpression<ItemStack>(ItemStack::class.java) {

    companion object {
        init {
            registerExpression<ConfigItemsForGuildExpression, ItemStack>(
                PROPERTY,
                "[required] items (to create|for) guild"
            )
        }

        fun defaultItemSet(): GuildItemSet {
            val itemsConfig = FunnyGuilds.getInstance().itemsConfiguration
            return itemsConfig.setsSortedByPriority.firstOrNull { it.permission == null } ?: GuildItemSet()
        }

        fun vipItemSet(): GuildItemSet {
            val itemsConfig = FunnyGuilds.getInstance().itemsConfiguration
            return itemsConfig.setsSortedByPriority.firstOrNull { it.permission != null } ?: defaultItemSet()
        }
    }

    override fun get(event: Event): Array<ItemStack> {
        return ItemUtils.buildRequiredItems(defaultItemSet(), FunnyGuilds.getInstance().itemsConfiguration).toTypedArray()
    }

    override fun isSingle(): Boolean {
        return false
    }



}
