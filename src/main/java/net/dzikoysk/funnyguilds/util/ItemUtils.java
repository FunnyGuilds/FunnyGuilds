package net.dzikoysk.funnyguilds.util;

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

            message = StringUtils.replace(message, "{ITEMS}", StringUtils.toString(translatedItems, true));
        }

        return message;
    }

    public static ItemStack[] toArray(Collection<ItemStack> collection) {
        return collection.toArray(new ItemStack[collection.size()]);
    }

    private ItemUtils() {

    }

}
