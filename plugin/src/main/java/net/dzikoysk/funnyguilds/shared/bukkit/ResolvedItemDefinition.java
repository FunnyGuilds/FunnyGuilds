package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

/** Validated values shared by item matching and item creation. */
public record ResolvedItemDefinition(
        @Nullable Material material,
        @Nullable Component name,
        List<Component> lore,
        Map<Enchantment, Integer> enchants,
        Set<ItemFlag> flags,
        @Nullable Integer customModelData,
        @Nullable String skullOwner,
        @Nullable Color armorColor
) {

    public static ResolvedItemDefinition resolve(GuildItemDefinition definition, String path) {
        Material material = isSet(definition.material)
                ? parse(path, "material", definition.material, () -> MaterialUtils.matchMaterial(definition.material)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown material")))
                : null;
        Component name = isSet(definition.name)
                ? parse(path, "name", definition.name, () -> ItemUtils.coloredWithoutItalics(definition.name))
                : null;
        List<Component> lore = definition.lore == null ? List.of()
                : parse(path, "lore", definition.lore, () -> definition.lore.stream()
                        .map(line -> isSet(line) ? ItemUtils.coloredWithoutItalics(line) : Component.empty())
                        .toList());
        Set<ItemFlag> flags = parse(path, "flags", definition.flags, () -> definition.getFlagsOrEmpty().stream()
                .map(flag -> {
                    if (!isSet(flag)) {
                        throw new IllegalArgumentException("Expected an item flag name");
                    }
                    return ItemFlag.valueOf(flag.toUpperCase(Locale.ROOT));
                })
                .collect(Collectors.toUnmodifiableSet()));
        Color color = isSet(definition.armorColor)
                ? parse(path, "armor-color", definition.armorColor, () -> parseColor(definition.armorColor))
                : null;
        return new ResolvedItemDefinition(material, name, lore, resolveEnchants(definition, path), flags,
                definition.customModelData, isSet(definition.skullOwner) ? definition.skullOwner : null, color);
    }

    private static Map<Enchantment, Integer> resolveEnchants(GuildItemDefinition definition, String path) {
        Map<Enchantment, Integer> enchants = new LinkedHashMap<>();
        List<String> configured = definition.getEnchantsOrEmpty();
        for (int index = 0; index < configured.size(); index++) {
            String value = configured.get(index);
            Map.Entry<Enchantment, Integer> enchant = parse(path, "enchants[" + index + "]", value, () -> {
                if (!isSet(value)) {
                    throw new IllegalArgumentException("Expected an enchantment name and optional level");
                }
                String[] parts = value.split(":", 2);
                Enchantment type = ItemUtils.matchEnchant(parts[0]);
                if (type == null) {
                    throw new IllegalArgumentException("Unknown enchantment");
                }
                int level = parts.length == 2 ? Integer.parseInt(parts[1]) : 1;
                if (level <= 0) {
                    throw new IllegalArgumentException("Enchantment level must be positive");
                }
                return Map.entry(type, level);
            });
            enchants.put(enchant.getKey(), enchant.getValue());
        }
        return Map.copyOf(enchants);
    }

    private static Color parseColor(String value) {
        String[] rgb = value.split("_", -1);
        if (rgb.length != 3) {
            throw new IllegalArgumentException("Expected R_G_B with components from 0 to 255");
        }
        return Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
    }

    private static <T> T parse(String path, String property, Object value, Supplier<T> parser) {
        try {
            return parser.get();
        }
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(path + "." + property + ": invalid value " + value
                    + " (" + exception.getMessage() + ")", exception);
        }
    }

    public ItemStack toItemStack(int amount) {
        ItemStack item = new ItemStack(this.material != null ? this.material : Material.STONE, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            applyTo(meta);
            item.setItemMeta(meta);
        }
        return item;
    }

    void applyTo(ItemMeta meta) {
        if (this.name != null) {
            meta.displayName(this.name);
        }
        if (!this.lore.isEmpty()) {
            meta.lore(this.lore);
        }
        this.enchants.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
        this.flags.forEach(meta::addItemFlags);
        if (this.customModelData != null) {
            meta.setCustomModelData(this.customModelData);
        }
        if (this.skullOwner != null && meta instanceof SkullMeta skull) {
            skull.setPlayerProfile(Bukkit.createProfile(this.skullOwner));
        }
        if (this.armorColor != null && meta instanceof LeatherArmorMeta armor) {
            armor.setColor(this.armorColor);
        }
    }

    private static boolean isSet(String value) {
        return value != null && !value.isEmpty();
    }

}
