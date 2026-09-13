package net.dzikoysk.funnyguilds.config.sections.upgrades;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class GuildUpgradesConfiguration extends OkaeriConfig {

    @Comment("Czy system ulepszeń gildii ma być włączony")
    public boolean enabled = false;

    @Comment("")
    @Comment("Typ waluty używanej do zakupu ulepszeń:")
    @Comment(" VAULT - płatność pieniędzmi (wymaga pluginu Vault i podłączonej wtyczki ekonomicznej)")
    @Comment(" ITEM - płatność przedmiotem, ustawianym komendą /funnyguilds additem upgrade <klucz> <poziom>")
    @CustomKey("economy-type")
    public EconomyType economyType = EconomyType.VAULT;

    @Comment("")
    @Comment("Lista dostępnych ulepszeń — klucz mapy musi odpowiadać jednemu z typów: POINTS_BOOST, MEMBERS (wielkość liter nie ma znaczenia)")
    @Comment("Pierwszy element listy 'levels' to poziom 1, kolejny to poziom 2 itd.")
    public Map<String, UpgradeDefinition> upgrades = defaultUpgrades();

    public GuildUpgradesConfiguration() {
    }

    public Map<String, UpgradeDefinition> getUpgrades() {
        return this.upgrades != null ? this.upgrades : Map.of();
    }

    public Optional<UpgradeDefinition> findUpgrade(String key) {
        if (key == null || this.upgrades == null) {
            return Optional.empty();
        }

        UpgradeDefinition exact = this.upgrades.get(key);
        if (exact != null) {
            return Optional.of(exact);
        }

        return this.upgrades.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(key))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public enum EconomyType {
        VAULT,
        ITEM
    }

    private static Map<String, UpgradeDefinition> defaultUpgrades() {
        Map<String, UpgradeDefinition> upgrades = new LinkedHashMap<>();

        upgrades.put("points_boost", new UpgradeDefinition(Arrays.asList(
                new UpgradeLevelDefinition(5.0, 5000.0),
                new UpgradeLevelDefinition(10.0, 15000.0),
                new UpgradeLevelDefinition(15.0, 30000.0)
        )));

        upgrades.put("members", new UpgradeDefinition(Arrays.asList(
                new UpgradeLevelDefinition(2.0, 10000.0),
                new UpgradeLevelDefinition(4.0, 25000.0),
                new UpgradeLevelDefinition(6.0, 45000.0)
        )));

        return upgrades;
    }

}
