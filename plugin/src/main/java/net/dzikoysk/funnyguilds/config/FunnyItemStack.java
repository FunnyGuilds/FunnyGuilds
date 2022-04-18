package net.dzikoysk.funnyguilds.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class FunnyItemStack {

    private final ItemStack item;
    private final String itemString;

    public FunnyItemStack(String itemString) {
        this.item = ItemUtils.parseItem(itemString);
        this.itemString = itemString;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getItemString() {
        return itemString;
    }

    public static List<FunnyItemStack> parseItemStacks(List<String> stringItemStacks) {
        return stringItemStacks.stream()
                .map(FunnyItemStack::new)
                .collect(Collectors.toList());
    }

    public static List<FunnyItemStack> parseItemStacks(String... stringItemStacks) {
        return parseItemStacks(Arrays.asList(stringItemStacks));
    }

    public static List<ItemStack> toItemStacks(List<FunnyItemStack> itemStacks) {
        return itemStacks.stream()
                .map(FunnyItemStack::getItem)
                .collect(Collectors.toList());
    }

}
