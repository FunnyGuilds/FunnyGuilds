package net.dzikoysk.funnyguilds.config;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

/**
 * Parsed, runtime representation of a single {@code explode-materials} scope (either {@code guild} or {@code global}).
 *
 * <p>Each scope is configured as a map of material -&gt; chance (in %), with a special {@code *} (wildcard) key
 * describing what should happen with materials that are not explicitly listed:</p>
 * <ul>
 *     <li>{@code none} - the material is never destroyed and blocks destroyed by the vanilla explosion are dropped from the result</li>
 *     <li>{@code default} - vanilla behaviour is kept (the material is not additionally destroyed by FunnyGuilds)</li>
 *     <li>{@code <number>} - every unlisted material has the given chance (in %) to be destroyed</li>
 * </ul>
 */
public class ExplodeMaterialsScope {

    public static final String WILDCARD_KEY = "*";
    public static final String GUILD_SCOPE = "guild";
    public static final String GLOBAL_SCOPE = "global";

    public enum WildcardMode {
        /** Unlisted materials are never destroyed and vanilla-destroyed blocks are dropped from this scope. */
        NONE,
        /** Unlisted materials follow the vanilla explosion behaviour. */
        DEFAULT,
        /** Unlisted materials are destroyed with {@link #wildcardChance} chance. */
        CHANCE
    }

    private final WildcardMode wildcardMode;
    private final double wildcardChance;
    private final Map<Material, Double> materials;

    public ExplodeMaterialsScope(WildcardMode wildcardMode, double wildcardChance, Map<Material, Double> materials) {
        this.wildcardMode = wildcardMode;
        this.wildcardChance = wildcardChance;
        this.materials = materials;
    }

    /**
     * @return whether blocks destroyed by the vanilla explosion should be dropped (not destroyed) in this scope
     */
    public boolean dropsVanillaBlocks() {
        return this.wildcardMode == WildcardMode.NONE;
    }

    /**
     * @param material the material to check
     * @return the chance (in %) the given material should be additionally destroyed by FunnyGuilds,
     * or {@code null} if it should not be additionally destroyed (deferred to vanilla / protected)
     */
    public @Nullable Double explosionChance(Material material) {
        Double explicit = this.materials.get(material);
        if (explicit != null) {
            return explicit;
        }

        if (this.wildcardMode == WildcardMode.CHANCE) {
            return this.wildcardChance;
        }

        return null;
    }

    public WildcardMode getWildcardMode() {
        return this.wildcardMode;
    }

    public double getWildcardChance() {
        return this.wildcardChance;
    }

    public Map<Material, Double> getMaterials() {
        return this.materials;
    }

    /**
     * Parses a raw scope map (material/wildcard -&gt; chance/keyword) into an {@link ExplodeMaterialsScope}.
     *
     * @param raw the raw scope map, may be {@code null}
     * @return the parsed scope
     */
    public static ExplodeMaterialsScope parse(@Nullable Map<String, Object> raw) {
        WildcardMode wildcardMode = WildcardMode.DEFAULT;
        double wildcardChance = 0.0;
        Map<Material, Double> materials = new EnumMap<>(Material.class);

        if (raw != null) {
            for (Entry<String, Object> entry : raw.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (WILDCARD_KEY.equals(key)) {
                    Wildcard wildcard = parseWildcard(value);
                    wildcardMode = wildcard.mode;
                    wildcardChance = wildcard.chance;
                    continue;
                }

                Double chance = parseChance(value);
                if (chance == null || chance < 0) {
                    continue;
                }

                Material material = Material.matchMaterial(key);
                if (material == null || material == Material.AIR) {
                    continue;
                }

                materials.put(material, chance);
            }
        }

        return new ExplodeMaterialsScope(wildcardMode, wildcardChance, materials);
    }

    /**
     * Converts the legacy (flat) {@code explode-materials} map into the new {@code guild}/{@code global} structure.
     *
     * @param legacy          the legacy flat map (material/wildcard -&gt; chance), may be {@code null}
     * @param affectOnlyGuild the legacy {@code explode-should-affect-only-guild} flag
     * @return the new nested configuration
     */
    public static Map<String, Object> convertLegacy(@Nullable Map<String, Object> legacy, boolean affectOnlyGuild) {
        Map<String, Object> nested = new LinkedHashMap<>();
        nested.put(GUILD_SCOPE, withDefaultWildcard(legacy));
        // When explosions previously affected only guild territory, nothing was destroyed outside of it.
        nested.put(GLOBAL_SCOPE, affectOnlyGuild ? noneScope() : withDefaultWildcard(legacy));
        return nested;
    }

    /**
     * @return the default {@code explode-materials} configuration, preserving the historical behaviour in both scopes
     */
    public static Map<String, Object> defaultConfiguration() {
        Map<String, Object> nested = new LinkedHashMap<>();
        nested.put(GUILD_SCOPE, defaultScope());
        nested.put(GLOBAL_SCOPE, defaultScope());
        return nested;
    }

    private static Map<String, Object> defaultScope() {
        Map<String, Object> scope = new LinkedHashMap<>();
        scope.put(WILDCARD_KEY, "default");
        scope.put("ender_chest", 20.0);
        scope.put("enchantment_table", 20.0);
        scope.put("obsidian", 20.0);
        scope.put("water", 33.0);
        scope.put("lava", 33.0);
        return scope;
    }

    private static Map<String, Object> noneScope() {
        Map<String, Object> scope = new LinkedHashMap<>();
        scope.put(WILDCARD_KEY, "none");
        return scope;
    }

    private static Map<String, Object> withDefaultWildcard(@Nullable Map<String, Object> legacy) {
        Map<String, Object> scope = new LinkedHashMap<>();
        if (legacy == null || !legacy.containsKey(WILDCARD_KEY)) {
            // No legacy wildcard means unlisted materials were deferred to vanilla.
            scope.put(WILDCARD_KEY, "default");
        }
        if (legacy != null) {
            scope.putAll(legacy);
        }
        return scope;
    }

    private static Wildcard parseWildcard(Object value) {
        if (value instanceof Number) {
            double chance = ((Number) value).doubleValue();
            return chance < 0 ? new Wildcard(WildcardMode.DEFAULT, 0.0) : new Wildcard(WildcardMode.CHANCE, chance);
        }

        String text = String.valueOf(value).trim();
        if (text.equalsIgnoreCase("none")) {
            return new Wildcard(WildcardMode.NONE, 0.0);
        }
        if (text.equalsIgnoreCase("default")) {
            return new Wildcard(WildcardMode.DEFAULT, 0.0);
        }

        try {
            double chance = Double.parseDouble(text);
            return chance < 0 ? new Wildcard(WildcardMode.DEFAULT, 0.0) : new Wildcard(WildcardMode.CHANCE, chance);
        }
        catch (NumberFormatException exception) {
            FunnyGuilds.getPluginLogger().parser("\"" + text + "\" is not a valid explode-materials wildcard value (expected 'none', 'default' or a number)");
            return new Wildcard(WildcardMode.DEFAULT, 0.0);
        }
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

    private static final class Wildcard {

        private final WildcardMode mode;
        private final double chance;

        private Wildcard(WildcardMode mode, double chance) {
            this.mode = mode;
            this.chance = chance;
        }

    }

}
