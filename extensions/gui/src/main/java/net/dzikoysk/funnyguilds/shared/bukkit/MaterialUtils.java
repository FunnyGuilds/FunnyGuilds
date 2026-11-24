package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Optional;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public final class MaterialUtils {

    private MaterialUtils() {
    }

    /** Resolves a Bukkit material name, returning empty for missing or unknown names. */
    public static Optional<Material> matchMaterial(@Nullable String name) {
        return Optional.ofNullable(name)
                .filter(value -> !value.isEmpty())
                .map(Material::matchMaterial);
    }

}
