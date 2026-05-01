package net.dzikoysk.funnyguilds.feature.items.gui;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.config.sections.items.ItemDisplayConfig;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult.ItemCountResult;
import net.dzikoysk.funnyguilds.shared.adventure.MiniLegacyHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GuiItemBuilder {

    private GuiItemBuilder() {}

    public static ItemStack buildSimple(GuildItemDefinition def) {
        return toItemStack(def, 1);
    }

    public static ItemStack toItemStack(GuildItemDefinition def, int amount) {
        if (def == null) return fallback();

        Material material = Material.matchMaterial(def.material);
        if (material == null) material = Material.STONE;

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        if (def.name != null && !def.name.isEmpty()) {
            meta.displayName(miniMessage(def.name));
        }

        if (def.lore != null && !def.lore.isEmpty()) {
            List<Component> lore = new ArrayList<>();
            for (String line : def.lore) {
                lore.add(miniMessage(line));
            }
            meta.lore(lore);
        }

        applyEnchants(meta, def.getEnchantsOrEmpty());
        applyFlags(meta, def.getFlagsOrEmpty());

        if (def.customModelData != null) {
            meta.setCustomModelData(def.customModelData);
        }

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack buildWithDisplay(GuildItemDefinition def, ItemDisplayConfig display, ItemCountResult counts) {
        if (def == null) return fallback();

        Material material = Material.matchMaterial(def.material);
        if (material == null) material = Material.STONE;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String rawName = (def.name != null && !def.name.isEmpty()) ? def.name : def.material;
        String prefix = display.namePrefix != null ? display.namePrefix : "";
        meta.displayName(miniMessage(prefix + rawName));

        List<Component> lore = new ArrayList<>();
        if (def.lore != null) {
            for (String line : def.lore) {
                lore.add(miniMessage(line));
            }
        }
        if (display.hasAdditionalLore()) {
            for (String line : display.additionalLore) {
                lore.add(miniMessage(resolvePlaceholders(line, counts)));
            }
        }
        if (!lore.isEmpty()) {
            meta.lore(lore);
        }

        applyEnchants(meta, def.getEnchantsOrEmpty());
        applyFlags(meta, def.getFlagsOrEmpty());

        if (display.glow) {
            Registry<Enchantment> enchantRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            Enchantment luck = enchantRegistry.get(NamespacedKey.minecraft("luck"));
            if (luck != null) {
                meta.addEnchant(luck, 1, true);
            }
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (def.customModelData != null) {
            meta.setCustomModelData(def.customModelData);
        }

        item.setItemMeta(meta);
        return item;
    }

    private static String resolvePlaceholders(String line, ItemCountResult counts) {
        if (counts == null) return line;
        return line
                .replace("{REQUIRED}", String.valueOf(counts.getRequired()))
                .replace("{INV}",      String.valueOf(counts.getInv()))
                .replace("{ENDER}",    String.valueOf(counts.getEnder()))
                .replace("{TOTAL}",    String.valueOf(counts.getTotal()))
                .replace("{MISSING}",  String.valueOf(counts.getMissing()));
    }

    private static void applyEnchants(ItemMeta meta, List<String> enchants) {
        for (String enchantStr : enchants) {
            String[] parts = enchantStr.split(":");
            if (parts.length < 1) continue;
            Enchantment enchant = RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.ENCHANTMENT)
                    .get(NamespacedKey.minecraft(parts[0].toLowerCase(Locale.ROOT)));
            if (enchant == null) continue;
            int level = parts.length >= 2 ? parseIntSafe(parts[1], 1) : 1;
            meta.addEnchant(enchant, level, true);
        }
    }

    private static void applyFlags(ItemMeta meta, List<String> flags) {
        for (String flagStr : flags) {
            try {
                ItemFlag flag = ItemFlag.valueOf(flagStr.toUpperCase(Locale.ROOT));
                meta.addItemFlags(flag);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return def; }
    }

    private static Component miniMessage(String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        return MiniLegacyHelper.miniMessage().deserialize(text);
    }

    private static ItemStack fallback() {
        return new ItemStack(Material.STONE);
    }

}

