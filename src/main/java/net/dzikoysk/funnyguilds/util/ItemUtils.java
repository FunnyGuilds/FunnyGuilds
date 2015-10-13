package net.dzikoysk.funnyguilds.util;

import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    public static boolean equals(ItemStack is1, ItemStack is2) {
        if (is1 == null && is2 == null)
            return true;
        if (is1 == null || is2 == null)
            return false;
        return is1.getType().equals(is2.getType()) && is1.getDurability() == is2.getDurability();
    }

}
