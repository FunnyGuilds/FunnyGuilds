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

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class ExplosionControlConfiguration extends OkaeriConfig {

    @Comment("Wybuchy na terenie gildii")
    public Scope guild = defaultScope();

    @Comment("")
    @Comment("Wybuchy poza terenem gildii")
    public Scope global = defaultScope();

    public void loadProcessedProperties() {
        this.guild.loadProcessedProperties();
        this.global.loadProcessedProperties();
    }

    private static Scope defaultScope() {
        Map<String, Double> materials = new LinkedHashMap<>();
        materials.put("ender_chest", 20.0);
        materials.put("enchantment_table", 20.0);
        materials.put("obsidian", 20.0);
        materials.put("water", 33.0);
        materials.put("lava", 33.0);
        return Scope.of(3, -1.0, materials);
    }

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class Scope extends OkaeriConfig {

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
                if (chance == null) {
                    return;
                }

                Material material = Material.matchMaterial(name);
                if (material != null && material != Material.AIR) {
                    // A negative chance is kept as an explicit "never destroy this material" marker.
                    resolved.put(material, chance);
                }
            });
            this.resolvedMaterials = resolved;
        }

        public int getRadius() {
            return this.radius;
        }

        public boolean protectsVanillaBlocks() {
            return this.defaultChance == 0.0;
        }

        public @Nullable Double explosionChance(Material material) {
            Double chance = this.resolvedMaterials.get(material);
            if (chance != null) {
                return chance < 0 ? null : chance;
            }
            return this.defaultChance > 0.0 ? this.defaultChance : null;
        }

        static Scope of(int radius, double defaultChance, Map<String, Double> materials) {
            Scope scope = new Scope();
            scope.radius = radius;
            scope.defaultChance = defaultChance;
            scope.materials = materials;
            return scope;
        }

    }

}
