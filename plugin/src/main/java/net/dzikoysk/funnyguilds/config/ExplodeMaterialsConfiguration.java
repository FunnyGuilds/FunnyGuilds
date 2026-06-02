package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Materials destroyed by explosions, split into a {@code guild} scope (blocks on guild territory)
 * and a {@code global} scope (blocks elsewhere).
 */
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class ExplodeMaterialsConfiguration extends OkaeriConfig {

    @Comment("Bloki niszczone po wybuchu na terenie gildii")
    public ExplodeMaterialsScope guild = defaultScope();

    @Comment("")
    @Comment("Bloki niszczone po wybuchu poza terenem gildii")
    public ExplodeMaterialsScope global = defaultScope();

    public void loadProcessedProperties() {
        this.guild.loadProcessedProperties();
        this.global.loadProcessedProperties();
    }

    private static ExplodeMaterialsScope defaultScope() {
        Map<String, Double> materials = new LinkedHashMap<>();
        materials.put("ender_chest", 20.0);
        materials.put("enchantment_table", 20.0);
        materials.put("obsidian", 20.0);
        materials.put("water", 33.0);
        materials.put("lava", 33.0);
        return ExplodeMaterialsScope.of(-1.0, materials);
    }

}
