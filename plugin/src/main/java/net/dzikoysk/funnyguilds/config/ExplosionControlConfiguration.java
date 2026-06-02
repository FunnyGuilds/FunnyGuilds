package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controls how explosions destroy blocks, split into a {@code guild} scope (explosions on guild territory)
 * and a {@code global} scope (explosions elsewhere). The active scope is chosen by the explosion's location.
 */
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class ExplosionControlConfiguration extends OkaeriConfig {

    @Comment("Wybuchy na terenie gildii")
    public ExplosionControlScope guild = defaultScope();

    @Comment("")
    @Comment("Wybuchy poza terenem gildii")
    public ExplosionControlScope global = defaultScope();

    public void loadProcessedProperties() {
        this.guild.loadProcessedProperties();
        this.global.loadProcessedProperties();
    }

    private static ExplosionControlScope defaultScope() {
        Map<String, Double> materials = new LinkedHashMap<>();
        materials.put("ender_chest", 20.0);
        materials.put("enchantment_table", 20.0);
        materials.put("obsidian", 20.0);
        materials.put("water", 33.0);
        materials.put("lava", 33.0);
        return ExplosionControlScope.of(3, -1.0, materials);
    }

}
