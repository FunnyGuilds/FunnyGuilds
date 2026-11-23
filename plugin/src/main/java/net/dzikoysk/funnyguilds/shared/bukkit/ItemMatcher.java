package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

/** Matches only properties supplied in an item definition, never the rest of Bukkit's item metadata. */
public final class ItemMatcher implements Predicate<ItemStack> {

    private final Material material;
    private final List<Predicate<ItemMeta>> metadataChecks;

    public ItemMatcher(GuildItemDefinition definition) {
        this.material = isSet(definition.material)
                ? MaterialUtils.matchMaterial(definition.material)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown material: " + definition.material))
                : null;
        List<Predicate<ItemMeta>> checks = new ArrayList<>();

        if (isSet(definition.name)) {
            Component name = ItemUtils.coloredWithoutItalics(definition.name);
            checks.add(meta -> Objects.equals(name, meta.displayName()));
        }
        if (definition.lore != null && !definition.lore.isEmpty()) {
            List<Component> lore = definition.lore.stream()
                    .map(line -> isSet(line) ? ItemUtils.coloredWithoutItalics(line) : Component.empty())
                    .toList();
            checks.add(meta -> Objects.equals(lore, meta.lore()));
        }
        if (!definition.getEnchantsOrEmpty().isEmpty()) {
            Map<Enchantment, Integer> enchants = definition.getEnchantsOrEmpty().stream()
                    .map(value -> value.split(":", 2))
                    .collect(Collectors.toUnmodifiableMap(
                            parts -> Objects.requireNonNull(ItemUtils.matchEnchant(parts[0]), "Unknown enchant: " + parts[0]),
                            parts -> parts.length == 2 ? Integer.parseInt(parts[1]) : 1,
                            (first, last) -> last
                    ));
            checks.add(meta -> enchants.equals(meta.getEnchants()));
        }
        if (!definition.getFlagsOrEmpty().isEmpty()) {
            Set<ItemFlag> flags = definition.getFlagsOrEmpty().stream()
                    .map(value -> ItemUtils.matchItemFlag(value)
                            .orElseThrow(() -> new IllegalArgumentException("Unknown item flag: " + value)))
                    .collect(Collectors.toUnmodifiableSet());
            checks.add(meta -> flags.equals(meta.getItemFlags()));
        }
        if (definition.customModelData != null) {
            int customModelData = definition.customModelData;
            checks.add(meta -> meta.hasCustomModelData() && meta.getCustomModelData() == customModelData);
        }
        if (isSet(definition.skullOwner)) {
            String owner = definition.skullOwner;
            checks.add(meta -> meta instanceof SkullMeta skull
                    && skull.getPlayerProfile() != null
                    && owner.equalsIgnoreCase(skull.getPlayerProfile().getName()));
        }
        if (isSet(definition.armorColor)) {
            String[] rgb = definition.armorColor.split("_");
            if (rgb.length != 3) {
                throw new IllegalArgumentException("Invalid armor-color (expected R_G_B): " + definition.armorColor);
            }
            Color color = Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
            checks.add(meta -> meta instanceof LeatherArmorMeta armor && color.equals(armor.getColor()));
        }
        this.metadataChecks = List.copyOf(checks);
    }

    @Override
    public boolean test(ItemStack item) {
        if (item == null || item.getAmount() <= 0) {
            return false;
        }
        switch (item.getType()) {
            case AIR, CAVE_AIR, VOID_AIR -> { return false; }
            default -> { }
        }
        if (this.material != null && this.material != item.getType()) {
            return false;
        }
        if (this.metadataChecks.isEmpty()) {
            return true;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && this.metadataChecks.stream().allMatch(check -> check.test(meta));
    }

    private static boolean isSet(String value) {
        return value != null && !value.isEmpty();
    }

}
