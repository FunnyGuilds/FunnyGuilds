package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Collection;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import panda.std.Pair;

public final class InventoryUtils {

    private InventoryUtils() { }

    /**
     * Add specified items to the player's inventory. If any items cannot be added due to no inventory space, they will
     * be dropped at the player's feet location.
     *
     * @param player target player
     * @param items  added items
     * @return a Pair representing counts of the handled items - first int representing items added, second int
     *         representing items dropped
     */
    public static Pair<Integer, Integer> addItemsWithOverflowDrop(Player player, Collection<ItemStack> items) {
        int itemsAdded = 0;
        int itemsDropped = 0;

        for (ItemStack item : items) {
            int overflowAmount = 0;

            Map<Integer, ItemStack> overflow = player.getInventory().addItem(item.clone());
            for (ItemStack overflowItem : overflow.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), overflowItem);
                overflowAmount += overflowItem.getAmount();
            }

            itemsAdded += (item.getAmount() - overflowAmount);
            itemsDropped += overflowAmount;
        }

        return Pair.of(itemsAdded, itemsDropped);
    }
}
