package net.dzikoysk.funnyguilds.feature.items.gui;

import java.util.Locale;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public final class IconMaterials {

    private IconMaterials() {
    }

    public static @Nullable Material resolve(@Nullable String name) {
        if (name == null) {
            return null;
        }
        Material material = Material.matchMaterial(name);
        if (material != null) {
            return material;
        }
        // The chain item was renamed to iron_chain in newer Minecraft versions.
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "chain", "minecraft:chain" -> Material.matchMaterial("IRON_CHAIN");
            case "iron_chain", "minecraft:iron_chain" -> Material.matchMaterial("CHAIN");
            default -> null;
        };
    }
}
