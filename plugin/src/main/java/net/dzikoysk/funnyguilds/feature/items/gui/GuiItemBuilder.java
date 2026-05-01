package net.dzikoysk.funnyguilds.feature.items.gui;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.config.sections.items.ItemDisplayConfig;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult.ItemCountResult;
import net.dzikoysk.funnyguilds.shared.adventure.MiniLegacyHelper;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
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
        return ItemUtils.toItemStack(def, 1);
    }

    public static ItemStack buildWithDisplay(GuildItemDefinition def, ItemDisplayConfig display, ItemCountResult counts) {
        if (def == null) return new ItemStack(Material.STONE);

        ItemStack item = ItemUtils.toItemStack(def, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String rawName = (def.name != null && !def.name.isEmpty()) ? def.name : def.material;
        String prefix = display.namePrefix != null ? display.namePrefix : "";
        meta.displayName(miniMessage(prefix + rawName));

        if (display.hasAdditionalLore()) {
            List<Component> lore = new ArrayList<>();
            if (def.lore != null) {
                for (String line : def.lore) {
                    lore.add(miniMessage(line));
                }
            }
            for (String line : display.additionalLore) {
                lore.add(miniMessage(resolvePlaceholders(line, counts)));
            }
            meta.lore(lore);
        }

        if (display.glow) {
            Registry<Enchantment> enchantRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            Enchantment luck = enchantRegistry.get(NamespacedKey.minecraft("luck"));
            if (luck != null) {
                meta.addEnchant(luck, 1, true);
            }
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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

    private static Component miniMessage(String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        return MiniLegacyHelper.miniMessage().deserialize(text);
    }

}
