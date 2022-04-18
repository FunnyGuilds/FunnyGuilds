package net.dzikoysk.funnyguilds.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class FunnyItemStack {

    private final ItemStack item;
    private final String itemString;

    private FunnyItemStack(ItemStack item, String itemString) {
        this.item = item;
        this.itemString = itemString;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getItemString() {
        return itemString;
    }

    public static FunnyItemStack parse(String itemString) {
        return new FunnyItemStack(ItemUtils.parseItem(itemString), itemString);
    }

    public static List<FunnyItemStack> parseItemStacks(List<String> stringItemStacks) {
        return stringItemStacks.stream()
                .map(FunnyItemStack::parse)
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
