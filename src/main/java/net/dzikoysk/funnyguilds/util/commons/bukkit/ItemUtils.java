package net.dzikoysk.funnyguilds.util.commons.bukkit;

import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public final class ItemUtils {

    public static String translatePlaceholder(String message, Collection<ItemStack> items, ItemStack item) {
        StringBuilder contentBuilder = new StringBuilder();

        if (message.contains("{ITEM}")) {
            contentBuilder.append(item.getAmount());
            contentBuilder.append(" ");
            contentBuilder.append(item.getType().toString().toLowerCase());

            message = StringUtils.replace(message, "{ITEM}", contentBuilder.toString());
        }

        if (message.contains("{ITEMS}")) {
            Collection<String> translatedItems = new ArrayList<>();

            for (ItemStack itemStack : items) {
                contentBuilder.setLength(0);

                contentBuilder.append(itemStack.getAmount());
                contentBuilder.append(" ");
                contentBuilder.append(itemStack.getType().toString().toLowerCase());

                translatedItems.add(contentBuilder.toString());
            }

            message = StringUtils.replace(message, "{ITEMS}", ChatUtils.toString(translatedItems, true));
        }

        return message;
    }

    public static int getItemAmount(ItemStack item, Inventory inv) {
        int amount = 0;
        
        for (ItemStack is : inv.getContents()) {
            if (item.isSimilar(is)) {
                amount += is.getAmount();
            }
        }
        
        return amount;
    }
    
    public static ItemStack[] toArray(Collection<ItemStack> collection) {
        return collection.toArray(new ItemStack[0]);
    }

    private ItemUtils() {}

}
