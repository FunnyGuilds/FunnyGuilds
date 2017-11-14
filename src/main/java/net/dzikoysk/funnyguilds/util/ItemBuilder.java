package net.dzikoysk.funnyguilds.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(final Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(final Material material, int stack) {
        this.itemStack = new ItemStack(material, stack);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(final Material material, int stack, int data) {
        this.itemStack = new ItemStack(material, stack, (short) data);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(final ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    private void refreshMeta() {
        this.itemStack.setItemMeta(itemMeta);
    }

    public ItemBuilder setName(String name, boolean color) {
        this.itemMeta.setDisplayName(color ? net.dzikoysk.funnyguilds.util.StringUtils.colored(name) : name);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        final List<String> formatted = new ArrayList<>();
        for (String str : lore) {
            formatted.add(StringUtils.replace(str, "&", "§"));
        }
        this.itemMeta.setLore(formatted);
        this.refreshMeta();
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        final List<String> formatted = new ArrayList<>();
        for (String str : lore) {
            formatted.add(StringUtils.replace(str, "&", "§"));
        }
        this.itemMeta.setLore(formatted);
        this.refreshMeta();
        return this;
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
