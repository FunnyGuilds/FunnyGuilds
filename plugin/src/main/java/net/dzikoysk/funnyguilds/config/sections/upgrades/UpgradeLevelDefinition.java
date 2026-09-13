package net.dzikoysk.funnyguilds.config.sections.upgrades;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.validator.annotation.DecimalMin;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class UpgradeLevelDefinition extends OkaeriConfig {

    @Comment("Wartość efektu tego poziomu — dla POINTS_BOOST procent bonusu, dla MEMBERS liczba dodatkowych miejsc")
    public double value = 0.0;

    @DecimalMin("0")
    @Comment("Koszt w walucie, używany gdy economy-type ustawione jest na VAULT")
    @CustomKey("vault-cost")
    public double vaultCost = 0.0;

    public UpgradeLevelDefinition() {
    }

    public UpgradeLevelDefinition(double value, double vaultCost) {
        this.value = value;
        this.vaultCost = vaultCost;
    }

}
