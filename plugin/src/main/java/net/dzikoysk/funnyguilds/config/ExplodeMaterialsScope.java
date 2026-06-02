package net.dzikoysk.funnyguilds.config;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

/**
 * Parsed runtime view of one {@code explode-materials} scope ({@code guild} or {@code global}).
 *
 * <p>Each scope has a {@code default} rule for materials that are not explicitly listed under
 * {@code overrides}: {@code none} (destroy nothing but the overridden materials and preserve blocks
 * the vanilla explosion would destroy), {@code vanilla} (vanilla behaviour) or a number (destroy every
 * other material with that chance). {@code overrides} maps individual materials to their chance (in %).
 */
public class ExplodeMaterialsScope {

    public static final String GUILD_SCOPE = "guild";
    public static final String GLOBAL_SCOPE = "global";
    public static final String DEFAULT_KEY = "default";
    public static final String OVERRIDES_KEY = "overrides";

    private final boolean dropVanillaBlocks;
    private final @Nullable Double defaultChance;
    private final Map<Material, Double> overrides;

    private ExplodeMaterialsScope(boolean dropVanillaBlocks, @Nullable Double defaultChance, Map<Material, Double> overrides) {
        this.dropVanillaBlocks = dropVanillaBlocks;
        this.defaultChance = defaultChance;
        this.overrides = overrides;
    }

    /** @return whether blocks destroyed by the vanilla explosion should be preserved (the {@code none} default) */
    public boolean dropsVanillaBlocks() {
        return this.dropVanillaBlocks;
    }

    /** @return the chance (in %) to destroy the material, or {@code null} to leave it to vanilla */
    public @Nullable Double explosionChance(Material material) {
        return this.overrides.getOrDefault(material, this.defaultChance);
    }

    public static ExplodeMaterialsScope parse(@Nullable Map<String, Object> scope) {
        boolean dropVanillaBlocks = false;
        Double defaultChance = null;
        Map<Material, Double> overrides = new EnumMap<>(Material.class);

        if (scope != null) {
            Object defaultRule = scope.get(DEFAULT_KEY);
            if (defaultRule != null) {
                String text = String.valueOf(defaultRule).trim();
                if (text.equalsIgnoreCase("none")) {
                    dropVanillaBlocks = true;
                }
                else if (!text.equalsIgnoreCase("vanilla")) {
                    Double chance = parseChance(defaultRule);
                    if (chance == null) {
                        FunnyGuilds.getPluginLogger().parser("\"" + text + "\" is not a valid explode-materials 'default' value (use 'none', 'vanilla' or a number)");
                    }
                    else if (chance >= 0) {
                        defaultChance = chance;
                    }
                }
            }

            for (Map.Entry<String, Object> entry : asMap(scope.get(OVERRIDES_KEY)).entrySet()) {
                Double chance = parseChance(entry.getValue());
                if (chance == null || chance < 0) {
                    continue;
                }

                Material material = Material.matchMaterial(entry.getKey());
                if (material != null && material != Material.AIR) {
                    overrides.put(material, chance);
                }
            }
        }

        return new ExplodeMaterialsScope(dropVanillaBlocks, defaultChance, overrides);
    }

    /**
     * Converts the legacy flat {@code explode-materials} map (and the removed
     * {@code explode-should-affect-only-guild} flag) into the new {@code guild}/{@code global} structure.
     */
    public static Map<String, Object> convertLegacy(@Nullable Map<String, Object> legacy, boolean affectOnlyGuild) {
        Map<String, Object> nested = new LinkedHashMap<>();
        nested.put(GUILD_SCOPE, scopeFromLegacy(legacy));
        // When explosions previously affected only guild territory, nothing was destroyed outside of it.
        nested.put(GLOBAL_SCOPE, affectOnlyGuild ? noneScope() : scopeFromLegacy(legacy));
        return nested;
    }

    /** @return the default configuration, preserving the historical behaviour in both scopes */
    public static Map<String, Object> defaultConfiguration() {
        Map<String, Object> nested = new LinkedHashMap<>();
        nested.put(GUILD_SCOPE, defaultScope());
        nested.put(GLOBAL_SCOPE, defaultScope());
        return nested;
    }

    /**
     * Coerces an okaeri value (which may be a {@link Map} or a Bukkit {@link ConfigurationSection})
     * into a plain map, or an empty map if it is neither.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(@Nullable Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        if (value instanceof ConfigurationSection) {
            return ((ConfigurationSection) value).getValues(false);
        }
        return Collections.emptyMap();
    }

    private static Map<String, Object> defaultScope() {
        Map<String, Object> overrides = new LinkedHashMap<>();
        overrides.put("ender_chest", 20.0);
        overrides.put("enchantment_table", 20.0);
        overrides.put("obsidian", 20.0);
        overrides.put("water", 33.0);
        overrides.put("lava", 33.0);
        return scope("vanilla", overrides);
    }

    private static Map<String, Object> noneScope() {
        return scope("none", new LinkedHashMap<>());
    }

    private static Map<String, Object> scopeFromLegacy(@Nullable Map<String, Object> legacy) {
        Object defaultRule = "vanilla";
        Map<String, Object> overrides = new LinkedHashMap<>();
        if (legacy != null) {
            for (Map.Entry<String, Object> entry : legacy.entrySet()) {
                if ("*".equals(entry.getKey())) {
                    // The legacy wildcard was always a number ("destroy every material with this chance").
                    defaultRule = entry.getValue();
                }
                else {
                    overrides.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return scope(defaultRule, overrides);
    }

    private static Map<String, Object> scope(Object defaultRule, Map<String, Object> overrides) {
        Map<String, Object> scope = new LinkedHashMap<>();
        scope.put(DEFAULT_KEY, defaultRule);
        scope.put(OVERRIDES_KEY, overrides);
        return scope;
    }

    private static @Nullable Double parseChance(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value).trim());
        }
        catch (NumberFormatException exception) {
            return null;
        }
    }

}
