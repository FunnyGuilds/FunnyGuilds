package net.dzikoysk.funnyguilds.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int stack) {
        this.itemStack = new ItemStack(material, stack);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int stack, int data) {
        this.itemStack = new ItemStack(material, stack, (short) data);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    private void refreshMeta() {
        this.itemStack.setItemMeta(itemMeta);
    }

    public ItemBuilder setName(String name, boolean color) {
        this.itemMeta.setDisplayName(color ? StringUtils.colored(name) : name);
        this.refreshMeta();
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {

        List<String> formatted = new ArrayList<>();

        for (String str : lore) {
            formatted.add(StringUtils.colored(str));
        }
        
        this.itemMeta.setLore(formatted);
        this.refreshMeta();
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder addEnchant(Enchantment enchant, int level) {
        this.itemMeta.addEnchant(enchant, level, true);
        this.refreshMeta();
        return this;
    }

    public ItemBuilder setFlag(ItemFlag flag) {
        this.itemMeta.addItemFlags(flag);
        this.refreshMeta();
        return this;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }
}
