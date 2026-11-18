package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

/** Matches only properties supplied in an item definition, never the rest of Bukkit's item metadata. */
public final class ItemMatcher implements Predicate<ItemStack> {

    private final Material material;
    private final List<Predicate<ItemMeta>> metadataChecks;

    public ItemMatcher(GuildItemDefinition definition) {
        ResolvedItemDefinition resolved = definition.getResolved();
        this.material = resolved.material();
        List<Predicate<ItemMeta>> checks = new ArrayList<>();

        if (resolved.name() != null) {
            checks.add(meta -> Objects.equals(resolved.name(), meta.displayName()));
        }
        if (!resolved.lore().isEmpty()) {
            checks.add(meta -> Objects.equals(resolved.lore(), meta.lore()));
        }
        if (!resolved.enchants().isEmpty()) {
            checks.add(meta -> resolved.enchants().equals(meta.getEnchants()));
        }
        if (!resolved.flags().isEmpty()) {
            checks.add(meta -> resolved.flags().equals(meta.getItemFlags()));
        }
        if (resolved.customModelData() != null) {
            checks.add(meta -> meta.hasCustomModelData() && meta.getCustomModelData() == resolved.customModelData());
        }
        if (resolved.skullOwner() != null) {
            checks.add(meta -> meta instanceof SkullMeta skull
                    && skull.getPlayerProfile() != null
                    && resolved.skullOwner().equalsIgnoreCase(skull.getPlayerProfile().getName()));
        }
        if (resolved.armorColor() != null) {
            checks.add(meta -> meta instanceof LeatherArmorMeta armor && resolved.armorColor().equals(armor.getColor()));
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

}
