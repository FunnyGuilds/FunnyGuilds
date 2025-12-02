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

public final class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(Material material, int stack) {
        this.itemStack = new ItemStack(material, stack);
    }

    @SuppressWarnings("deprecation") // konstruktor z data jest deprecated, ale zostaje dla zgodności
    public ItemBuilder(Material material, int stack, int data) {
        this.itemStack = new ItemStack(material, stack, (short) data);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder setName(String name, boolean color) {
        if (name != null) {
            Component displayName = color
                    ? LegacyComponentSerializer.legacySection().deserialize(name)
                    : Component.text(name);
            this.itemStack.editMeta(meta -> meta.displayName(displayName));
        }
        return this;
    }

    public ItemBuilder setLore(Iterable<String> lore, boolean color) {
        if (lore != null) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(color
                        ? LegacyComponentSerializer.legacySection().deserialize(line)
                        : Component.text(line));
            }
            this.itemStack.editMeta(meta -> meta.lore(loreComponents));
        }
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return this.setLore(Arrays.asList(lore), true);
    }

    public ItemBuilder addEnchant(Enchantment enchant, int level) {
        this.itemStack.editMeta(meta -> meta.addEnchant(enchant, level, true));
        return this;
    }

    public ItemBuilder setFlag(ItemFlag flag) {
        this.itemStack.editMeta(meta -> meta.addItemFlags(flag));
        return this;
    }

    public ItemBuilder setSkullOwner(String ownerName) {
        this.itemStack.editMeta(meta -> {
            if (meta instanceof SkullMeta skullMeta) {
                OfflinePlayer owner = org.bukkit.Bukkit.getOfflinePlayerIfCached(ownerName);
                if (owner == null) {
                    owner = org.bukkit.Bukkit.getOfflinePlayer(ownerName);
                }
                skullMeta.setOwningPlayer(owner);
            }
        });
        return this;
    }

    /**
     * Zwraca aktualne ItemMeta z itemStack.
     */
    public ItemMeta getMeta() {
        return this.itemStack.getItemMeta();
    }

    /**
     * Zwraca budowany ItemStack.
     */
    public ItemStack getItem() {
        return this.itemStack;
    }
}