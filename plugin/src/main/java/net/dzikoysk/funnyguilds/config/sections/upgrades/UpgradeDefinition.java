package net.dzikoysk.funnyguilds.config.sections.upgrades;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class UpgradeDefinition extends OkaeriConfig {

    @Comment("Kolejne poziomy tego ulepszenia — pierwszy element listy to poziom 1")
    public List<UpgradeLevelDefinition> levels = new ArrayList<>();

    public UpgradeDefinition() {
    }

    public UpgradeDefinition(List<UpgradeLevelDefinition> levels) {
        this.levels = levels;
    }

    public List<UpgradeLevelDefinition> getLevels() {
        return this.levels != null ? this.levels : Collections.emptyList();
    }

    public int getMaxLevel() {
        return this.getLevels().size();
    }

    public Optional<UpgradeLevelDefinition> getLevel(int level) {
        List<UpgradeLevelDefinition> definitions = this.getLevels();
        if (level < 1 || level > definitions.size()) {
            return Optional.empty();
        }

        return Optional.of(definitions.get(level - 1));
    }

}
