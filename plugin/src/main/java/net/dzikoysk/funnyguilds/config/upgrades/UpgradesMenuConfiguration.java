package net.dzikoysk.funnyguilds.config.upgrades;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.validator.annotation.Max;
import eu.okaeri.validator.annotation.Min;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class UpgradesMenuConfiguration extends OkaeriConfig {

    @Comment("Czy menu ulepszeń ma być włączone")
    public boolean enabled = true;

    @Comment("")
    @Comment("Tytuł menu")
    public String title = "<dark_gray><bold>Ulepszenia gildii";

    @Min(1)
    @Max(6)
    @Comment("")
    @Comment("Liczba wierszy menu (1-6)")
    public int rows = 3;

    @Comment("")
    @Comment("Wzorzec menu")
    @Comment("Placeholder {klucz_ulepszenia} pokazuje dane ulepszenie, {item-x} to filler, {close:item-c} to przycisk zamknięcia")
    public List<String> pattern = new ArrayList<>(Arrays.asList(
            "{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}",
            "{item-x}{points_boost}{item-x}{members}{item-x}{item-x}{item-x}{item-x}{item-x}",
            "{item-x}{item-x}{item-x}{item-x}{close:item-c}{item-x}{item-x}{item-x}{item-x}"
    ));

    @Comment("")
    @Comment("Item używany jako filler ({item-x})")
    public GuildItemDefinition filler = defaultFiller();

    @Comment("")
    @Comment("Item używany jako przycisk zamknięcia menu ({close:item-c})")
    public GuildItemDefinition close = defaultClose();

    @Comment("")
    @Comment("Wygląd poszczególnych ulepszeń")
    @Comment("Klucz = klucz ulepszenia z guild-upgrades.upgrades w config.yml")
    public Map<String, UpgradesMenuItemConfig> items = defaultItems();

    public UpgradesMenuConfiguration() {
    }

    public UpgradesMenuItemConfig getItem(String key) {
        if (this.items != null && this.items.containsKey(key)) {
            return this.items.get(key);
        }
        return new UpgradesMenuItemConfig();
    }

    private static GuildItemDefinition defaultFiller() {
        GuildItemDefinition filler = new GuildItemDefinition("BLACK_STAINED_GLASS_PANE");
        filler.name = " ";
        return filler;
    }

    private static GuildItemDefinition defaultClose() {
        GuildItemDefinition close = new GuildItemDefinition("BARRIER");
        close.name = "<red><bold>Zamknij";
        close.lore = new ArrayList<>(List.of("<gray>Kliknij aby zamknąć"));
        return close;
    }

    private static Map<String, UpgradesMenuItemConfig> defaultItems() {
        Map<String, UpgradesMenuItemConfig> items = new LinkedHashMap<>();

        UpgradesMenuItemConfig pointsBoost = new UpgradesMenuItemConfig();
        pointsBoost.material = "NETHER_STAR";
        pointsBoost.name = "<gold><bold>Bonus punktów rankingowych";
        pointsBoost.lore = new ArrayList<>(Arrays.asList(
                "<gray>Zwiększa punkty rankingowe zdobywane za zabójstwa",
                "",
                "<gray>Poziom: <white>{LEVEL}<gray>/<white>{MAX_LEVEL}",
                "<gray>Obecny bonus: <green>+{VALUE}%",
                "<gray>Następny poziom: <green>+{NEXT_VALUE}%",
                "<gray>Koszt: <yellow>{COST}",
                "",
                "<yellow>Kliknij aby ulepszyć"
        ));
        pointsBoost.maxedLore = new ArrayList<>(Arrays.asList(
                "<gray>Zwiększa punkty rankingowe zdobywane za zabójstwa",
                "",
                "<gray>Poziom: <white>{LEVEL}<gray>/<white>{MAX_LEVEL}",
                "<gray>Obecny bonus: <green>+{VALUE}%",
                "",
                "<green>Osiągnięto maksymalny poziom!"
        ));
        items.put("points_boost", pointsBoost);

        UpgradesMenuItemConfig members = new UpgradesMenuItemConfig();
        members.material = "PLAYER_HEAD";
        members.name = "<aqua><bold>Limit członków gildii";
        members.lore = new ArrayList<>(Arrays.asList(
                "<gray>Zwiększa maksymalną liczbę członków gildii",
                "",
                "<gray>Poziom: <white>{LEVEL}<gray>/<white>{MAX_LEVEL}",
                "<gray>Obecny bonus: <green>+{VALUE}",
                "<gray>Następny poziom: <green>+{NEXT_VALUE}",
                "<gray>Koszt: <yellow>{COST}",
                "",
                "<yellow>Kliknij aby ulepszyć"
        ));
        members.maxedLore = new ArrayList<>(Arrays.asList(
                "<gray>Zwiększa maksymalną liczbę członków gildii",
                "",
                "<gray>Poziom: <white>{LEVEL}<gray>/<white>{MAX_LEVEL}",
                "<gray>Obecny bonus: <green>+{VALUE}",
                "",
                "<green>Osiągnięto maksymalny poziom!"
        ));
        items.put("members", members);

        return items;
    }

}
