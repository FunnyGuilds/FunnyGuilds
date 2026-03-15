package net.dzikoysk.funnyguilds.config.sections.items;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Konfiguracja GUI przedmiotów gildii.
 */
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class GuiConfiguration extends OkaeriConfig {

    /** Czy GUI jest włączone. Gdy {@code false} — wymagania pokazywane tekstowo. */
    public boolean enabled = true;

    /** Tytuł GUI (MiniMessage). Placeholder: {@code {SET}} — nazwa setu. */
    public String title = "<dark_gray><bold>Przedmioty na gildie <gray>({SET})";

    /** Liczba wierszy GUI (1-6). */
    public int rows = 6;

    /**
     * Pattern GUI — lista wierszy, każdy zawiera tokeny w {@code {nawiasach}}.
     */
    public List<String> pattern = new ArrayList<>(Arrays.asList(
            "{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}",
            "{item-x}{item-1}{item-x}{item-2}{item-x}{item-3}{item-x}{item-4}{item-x}",
            "{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}",
            "{item-x}{item-5}{item-x}{item-6}{item-x}{item-7}{item-x}{item-8}{item-x}",
            "{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}{item-x}",
            "{item-x}{item-x}{toggle:item-t}{item-x}{summary:item-s}{item-x}{item-x}{item-x}{close:item-c}"
    ));

    /** Konfiguracja wyświetlania itemów (has-enough / missing). */
    public ItemDisplayGroup itemDisplay = new ItemDisplayGroup();

    /** Nadpisania per-set. Klucz = nazwa setu z guild-item-sets. */
    public Map<String, PerSetConfig> perSet = new LinkedHashMap<>();

    public GuiConfiguration() {
    }

    /**
     * Zwraca konfigurację per-set dla danego setu lub pustą instancję.
     */
    public PerSetConfig getPerSetConfig(String setName) {
        if (perSet != null && perSet.containsKey(setName)) {
            return perSet.get(setName);
        }
        return new PerSetConfig();
    }

    /**
     * Grupa konfiguracji wyświetlania — dwa warianty.
     */
    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class ItemDisplayGroup extends OkaeriConfig {

        /** Wyświetlanie gdy gracz posiada wystarczającą ilość itemu. */
        public ItemDisplayConfig hasEnough = new ItemDisplayConfig();

        /** Wyświetlanie gdy graczowi brakuje itemu. */
        public ItemDisplayConfig missing = new ItemDisplayConfig();

        public ItemDisplayGroup() {
            // Defaults for has-enough
            this.hasEnough.namePrefix = "<green>✔ ";
            this.hasEnough.additionalLore = new ArrayList<>(Arrays.asList(
                    "",
                    "<gray>Wymagane: <green>{REQUIRED}",
                    "<gray>Ekwipunek: <green>{INV}",
                    "<gray>Ender chest: <green>{ENDER}",
                    "<gray>Łącznie: <green>{TOTAL}"
            ));

            // Defaults for missing
            this.missing.namePrefix = "<red>✘ ";
            this.missing.additionalLore = new ArrayList<>(Arrays.asList(
                    "",
                    "<gray>Wymagane: <red>{REQUIRED}",
                    "<gray>Brakuje: <red>{MISSING}",
                    "",
                    "<red>Ekwipunek: <white>{INV}",
                    "<red>Ender chest: <white>{ENDER}",
                    "<red>Łącznie: <white>{TOTAL}"
            ));
        }

    }

}

