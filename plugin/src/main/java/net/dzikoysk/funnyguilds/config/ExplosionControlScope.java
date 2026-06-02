package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.validator.annotation.Min;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

/**
 * Explosion profile for a single scope ({@code guild} - blocks on guild territory, or {@code global} - elsewhere).
 *
 * <p>The {@code default} value controls materials that are not listed under {@code materials}:</p>
 * <ul>
 *     <li>{@code -1} - vanilla behaviour (FunnyGuilds does not touch them)</li>
 *     <li>{@code 0} - destroy nothing but the listed materials, and preserve blocks the vanilla explosion would destroy</li>
 *     <li>{@code > 0} - destroy every other material with the given chance (in %)</li>
 * </ul>
 */
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class ExplosionControlScope extends OkaeriConfig {

    @Min(0)
    @Comment("Zasięg pobieranych przedmiotów po wybuchu, jeżeli chcesz wyłączyć - wpisz 0")
    public int radius = 3;

    @Comment("")
    @Comment("Zachowanie dla materiałów spoza listy 'materials':")
    @Comment("-1  = zachowanie domyślne (vanilla)")
    @Comment(" 0  = nie niszcz nic poza listą oraz pomiń bloki niszczone domyślnie przez wybuch (ochrona terenu)")
    @Comment(">0  = szansa (w %) na zniszczenie każdego innego materiału")
    @CustomKey("default")
    public double defaultChance = -1.0;

    @Comment("")
    @Comment("Materiały i ich indywidualna szansa zniszczenia (w %)")
    public Map<String, Double> materials = new LinkedHashMap<>();

    @Exclude
    private Map<Material, Double> resolvedMaterials = Collections.emptyMap();

    public void loadProcessedProperties() {
        Map<Material, Double> resolved = new EnumMap<>(Material.class);
        this.materials.forEach((name, chance) -> {
            if (chance == null || chance < 0) {
                return;
            }

            Material material = Material.matchMaterial(name);
            if (material != null && material != Material.AIR) {
                resolved.put(material, chance);
            }
        });
        this.resolvedMaterials = resolved;
    }

    public int getRadius() {
        return this.radius;
    }

    /** @return whether blocks destroyed by the vanilla explosion should be preserved ({@code default: 0}) */
    public boolean dropsVanillaBlocks() {
        return this.defaultChance == 0.0;
    }

    /** @return the chance (in %) to destroy the material, or {@code null} to leave it to vanilla */
    public @Nullable Double explosionChance(Material material) {
        Double chance = this.resolvedMaterials.get(material);
        if (chance != null) {
            return chance;
        }
        return this.defaultChance > 0.0 ? this.defaultChance : null;
    }

    static ExplosionControlScope of(int radius, double defaultChance, Map<String, Double> materials) {
        ExplosionControlScope scope = new ExplosionControlScope();
        scope.radius = radius;
        scope.defaultChance = defaultChance;
        scope.materials = materials;
        return scope;
    }

}
