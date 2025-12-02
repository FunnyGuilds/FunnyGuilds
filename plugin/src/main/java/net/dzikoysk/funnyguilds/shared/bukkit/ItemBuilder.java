package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import panda.std.stream.PandaStream;

public final class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int stack) {
        this.itemStack = new ItemStack(material, stack);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int stack, int data) {
        this.itemStack = new ItemStack(material, stack, (short) data);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public void refreshMeta() {
        this.itemStack.setItemMeta(this.itemMeta);
    }

    // --- Set display name (Adventure API, nie deprecated) ---
    public ItemBuilder setName(String name, boolean color) {
        if (name != null) {
            Component displayName = color
                    ? LegacyComponentSerializer.legacySection().deserialize(name)
                    : Component.text(name);
            this.itemMeta.displayName(displayName);
            this.refreshMeta();
        }
        return this;
    }

    // --- Set lore (Adventure API, nie deprecated) ---
    public ItemBuilder setLore(Iterable<String> lore, boolean color) {
        if (lore != null) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(color
                        ? LegacyComponentSerializer.legacySection().deserialize(line)
                        : Component.text(line));
            }
            this.itemMeta.lore(loreComponents);
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return this.setLore(Arrays.asList(lore), true);
    }

    // --- Enchantments i flagi ---
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

    public ItemBuilder setSkullOwner(String ownerName) {
        if (this.itemMeta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) this.itemMeta;
            OfflinePlayer owner = org.bukkit.Bukkit.getOfflinePlayerIfCached(ownerName);
            if (owner == null) {
                owner = org.bukkit.Bukkit.getOfflinePlayer(ownerName);
            }
            skullMeta.setOwningPlayer(owner);
            this.refreshMeta();
        }
        return this;
    }

    // --- Gettery ---
    public ItemStack getItem() {
        return this.itemStack;
    }

    public ItemMeta getMeta() {
        return this.itemMeta;
    }
}
