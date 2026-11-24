package net.dzikoysk.funnyguilds.config.sections.items;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.exception.OkaeriException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.dzikoysk.funnyguilds.FunnyGuilds;

/**
 * Główna klasa konfiguracji {@code items.yml}.
 */
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("    FunnyGuilds — Przedmioty     #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header(" ")
@Header("Stary system (items/items-vip/requiredExperience/requiredMoney/rankCreate z config.yml)")
@Header("został zastąpiony tym plikiem. BRAK MIGRACJI — skonfiguruj od nowa.")
@Header(" ")
@Header("Tekst w name/lore: wyłącznie MiniMessage (https://docs.adventure.kyori.net/minimessage/format.html)")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class ItemsConfiguration extends OkaeriConfig {

    @Exclude
    private volatile ItemsConfiguration loadedSnapshot;

    /** Capture once per operation. Reloads replace the snapshot without modifying previous snapshots. */
    public ItemsConfiguration snapshot() {
        ItemsConfiguration current = this.loadedSnapshot;
        return current != null ? current : this;
    }

    @Comment("")
    @Comment("=======================================")
    @Comment(" BIBLIOTEKA PRZEDMIOTÓW")
    @Comment("=======================================")
    @Comment("Wszystkie itemy — zarówno wymagane do gildii jak i GUI — definiowane tu.")
    @Comment("Klucz = unikalna nazwa, referencjonowana w setach i patternie GUI.")
    @Comment("Wszystkie właściwości są opcjonalne, także material. Sprawdzane są tylko ustawione pola:")
    @Comment("material, name, lore, enchants, flags, custom-model-data, skull-owner, armor-color.")
    @Comment("Pominięte pola, null, puste teksty i puste listy nie ograniczają dopasowania.")
    @Comment("Ustawione właściwości muszą pasować dokładnie (w tym całe listy zaklęć i flag).")
    @Comment("Inne dane przedmiotu są ignorowane. Bez material GUI i nadawanie używają STONE.")
    @Comment("Przedmioty są pobierane z głównego ekwipunku i paska, bez zbroi i drugiej ręki.")
    @Comment("Jeden przedmiot może opłacić tylko jedno wymaganie, nawet jeśli pasuje do kilku.")
    @Comment("Definicje są sprawdzane podczas wczytywania i /funnyguilds reload.")
    @Comment("Błędna wartość odrzuca konfigurację z nazwą pola; podczas przeładowania zachowana jest poprzednia.")
    public volatile Map<String, GuildItemDefinition> guildItemLibrary = defaultLibrary();

    @Comment("")
    @Comment("=======================================")
    @Comment(" BIBLIOTEKA PRZYCISKÓW TOGGLE")
    @Comment("=======================================")
    @Comment("Itemy z dwoma stanami (all / missing-only) — używane w GUI jako przycisk toggle widoku.")
    public volatile Map<String, GuildItemToggleDefinition> guildToggleLibrary = defaultToggleLibrary();

    @Comment("")
    @Comment("=======================================")
    @Comment(" SETY PRZEDMIOTÓW")
    @Comment("=======================================")
    @Comment("Sety sortowane malejąco po priority — pierwszy pasujący (gracz ma permisję) wygrywa.")
    @Comment("Set bez permission (null) = domyślny, dostępny dla wszystkich.")
    public volatile Map<String, GuildItemSet> guildItemSets = defaultSets();

    @Comment("")
    @Comment("=======================================")
    @Comment(" BYPASS ADMINISTRACYJNY")
    @Comment("=======================================")
    @Comment("Gracz z tą permisją pomija WSZYSTKIE wymagania (itemy, exp, money, rank)")
    public volatile String adminItemsBypassPermission = "funnyguilds.items.admin.bypass";

    @Comment("")
    @Comment("=======================================")
    @Comment(" GUI")
    @Comment("=======================================")
    public volatile GuiConfiguration gui = new GuiConfiguration();

    // -------------------------------------------------------
    // API
    // -------------------------------------------------------

    /**
     * Pobiera definicję itemu z library po kluczu.
     * Sprawdza najpierw zwykłe itemy, potem toggle (zwraca stan "all").
     */
    public Optional<GuildItemDefinition> getLibraryItem(String key) {
        if (guildItemLibrary != null) {
            GuildItemDefinition direct = guildItemLibrary.get(key);
            if (direct != null) return Optional.of(direct);
        }
        if (guildToggleLibrary != null) {
            GuildItemToggleDefinition toggle = guildToggleLibrary.get(key);
            if (toggle != null) return Optional.ofNullable(toggle.all);
        }
        return Optional.empty();
    }

    /**
     * Pobiera definicję toggle itemu z library.
     */
    public Optional<GuildItemToggleDefinition> getToggleItem(String key) {
        if (guildToggleLibrary == null) return Optional.empty();
        return Optional.ofNullable(guildToggleLibrary.get(key));
    }

    /**
     * Rozwiązuje item — najpierw sprawdza per-set overrides, potem library.
     */
    public Optional<GuildItemDefinition> resolveItem(String key, PerSetConfig perSet) {
        if (perSet != null && perSet.libraryOverrides != null) {
            GuildItemDefinition override = perSet.libraryOverrides.get(key);
            if (override != null) return Optional.of(override);
        }
        return getLibraryItem(key);
    }

    /**
     * Zwraca sety posortowane malejąco po priority.
     */
    public List<GuildItemSet> getSetsSortedByPriority() {
        if (guildItemSets == null) return List.of();
        List<GuildItemSet> sorted = new ArrayList<>(guildItemSets.values());
        sorted.sort(Comparator.comparingInt((GuildItemSet s) -> s.priority).reversed());
        return sorted;
    }

    /**
     * Zwraca mapę setów.
     */
    public Map<String, GuildItemSet> getGuildItemSets() {
        return guildItemSets != null ? guildItemSets : Map.of();
    }

    public Optional<GuildItemSet> findGuildItemSet(String name, boolean ignoreCase) {
        if (guildItemSets == null || guildItemSets.isEmpty()) {
            return Optional.empty();
        }

        GuildItemSet exactSet = guildItemSets.get(name);
        if (exactSet != null) {
            return Optional.of(exactSet);
        }

        if (ignoreCase) {
            return guildItemSets.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(name))
                .map(Map.Entry::getValue)
                .findFirst();
        }

        return Optional.empty();
    }

    @Override
    public synchronized OkaeriConfig load(InputStream input) throws OkaeriException {
        // Reload runs asynchronously. Keep unvalidated values away from command and GUI readers.
        ItemsConfiguration candidate = new ItemsConfiguration();
        candidate.setConfigurer(this.getConfigurer());
        candidate.setBindFile(this.getBindFile());
        candidate.getContext().setValidator(this.getContext().getValidator());
        candidate.getContext().setLogger(this.getContext().getLogger());
        candidate.getContext().setErrorComments(this.getContext().isErrorComments());
        candidate.getContext().setRemoveOrphans(this.getContext().isRemoveOrphans());
        candidate.loadCandidate(input);
        publish(candidate);
        return this;
    }

    private void loadCandidate(InputStream input) {
        super.load(input);
        loadProcessedProperties();
    }

    public void loadProcessedProperties() {
        resolveLibrary(this.guildItemLibrary, "items.yml.guild-item-library");
        if (this.guildToggleLibrary != null) {
            this.guildToggleLibrary.forEach((key, toggle) -> {
                if (toggle == null) {
                    throw new IllegalArgumentException("items.yml.guild-toggle-library." + key + ": missing definition");
                }
                resolveDefinition(toggle.all, "items.yml.guild-toggle-library." + key + ".all");
                resolveDefinition(toggle.missingOnly, "items.yml.guild-toggle-library." + key + ".missing-only");
            });
        }
        if (this.gui != null && this.gui.perSet != null) {
            this.gui.perSet.forEach((key, perSet) -> {
                if (perSet != null) {
                    resolveLibrary(perSet.libraryOverrides, "items.yml.gui.per-set." + key + ".library-overrides");
                }
            });
        }
    }

    private static void resolveLibrary(Map<String, GuildItemDefinition> library, String path) {
        if (library != null) {
            library.forEach((key, definition) -> resolveDefinition(definition, path + "." + key));
        }
    }

    private static void resolveDefinition(GuildItemDefinition definition, String path) {
        if (definition == null) {
            throw new IllegalArgumentException(path + ": missing definition");
        }
        definition.resolve(path);
    }

    private void publish(ItemsConfiguration candidate) {
        // Keep the public YAML fields available for configuration access and saving.
        this.guildItemLibrary = candidate.guildItemLibrary;
        this.guildToggleLibrary = candidate.guildToggleLibrary;
        this.guildItemSets = candidate.guildItemSets;
        this.adminItemsBypassPermission = candidate.adminItemsBypassPermission;
        this.gui = candidate.gui;
        this.setInternalState(candidate.getInternalState());
        this.getContext().setRawContent(candidate.getContext().getRawContent());
        this.loadedSnapshot = candidate;
    }

    @Override
    public OkaeriConfig save() throws OkaeriException {
        propagateConfigurer();
        return super.save();
    }

    @Override
    public OkaeriConfig saveDefaults() throws OkaeriException {
        propagateConfigurer();
        return super.saveDefaults();
    }

    /**
     * OkaeriConfig automatycznie propaguje configurer do sub-configów będących bezpośrednimi polami,
     * ale NIE do sub-configów wewnątrz Map. Ta metoda robi to ręcznie.
     */
    private void propagateConfigurer() {
        Configurer configurer = this.getConfigurer();
        if (configurer == null) return;

        if (guildItemLibrary != null) {
            for (GuildItemDefinition def : guildItemLibrary.values()) {
                initSubConfig(def, configurer);
            }
        }
        if (guildToggleLibrary != null) {
            for (GuildItemToggleDefinition toggle : guildToggleLibrary.values()) {
                initSubConfig(toggle, configurer);
                if (toggle.all != null) initSubConfig(toggle.all, configurer);
                if (toggle.missingOnly != null) initSubConfig(toggle.missingOnly, configurer);
            }
        }
        if (guildItemSets != null) {
            for (GuildItemSet set : guildItemSets.values()) {
                initSubConfig(set, configurer);
                if (set.requirements != null) initSubConfig(set.requirements, configurer);
            }
        }
        if (gui != null) {
            if (gui.itemDisplay != null) {
                initSubConfig(gui.itemDisplay, configurer);
                if (gui.itemDisplay.hasEnough != null) initSubConfig(gui.itemDisplay.hasEnough, configurer);
                if (gui.itemDisplay.missing != null) initSubConfig(gui.itemDisplay.missing, configurer);
            }
            if (gui.perSet != null) {
                for (PerSetConfig perSet : gui.perSet.values()) {
                    initSubConfig(perSet, configurer);
                    if (perSet.libraryOverrides != null) {
                        for (GuildItemDefinition override : perSet.libraryOverrides.values()) {
                            initSubConfig(override, configurer);
                        }
                    }
                }
            }
        }
    }

    private static void initSubConfig(OkaeriConfig subConfig, Configurer configurer) {
        if (subConfig.getConfigurer() == null) {
            subConfig.withConfigurer(configurer);
        }
        try {
            subConfig.updateDeclaration();
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().debug(
                    "Failed to update declaration for sub-config " + subConfig.getClass().getSimpleName() + ": " + exception.getMessage()
            );
        }
    }

    // -------------------------------------------------------
    // Defaults
    // -------------------------------------------------------

    private static Map<String, GuildItemDefinition> defaultLibrary() {
        Map<String, GuildItemDefinition> library = new LinkedHashMap<>();

        // Required-item entries constrain only material; all unconfigured metadata is ignored.
        library.put("diamonds", new GuildItemDefinition("DIAMOND"));
        library.put("emeralds", new GuildItemDefinition("EMERALD"));
        library.put("iron", new GuildItemDefinition("IRON_INGOT"));

        GuildItemDefinition itemX = new GuildItemDefinition("BLACK_STAINED_GLASS_PANE");
        itemX.name = " ";
        library.put("item-x", itemX);

        GuildItemDefinition summary = new GuildItemDefinition("BOOK");
        summary.name = "<yellow><bold>Podsumowanie";
        summary.lore = new ArrayList<>(List.of(
                "<gray>Twój status:",
                "",
                "{MONEY_LINE}",
                "{LEVEL_LINE}",
                "{RANK_LINE}",
                "",
                "{STATUS}"
        ));
        library.put("item-s", summary);

        GuildItemDefinition close = new GuildItemDefinition("BARRIER");
        close.name = "<red><bold>Zamknij";
        close.lore = new ArrayList<>(List.of("<gray>Kliknij aby zamknąć"));
        library.put("item-c", close);

        return library;
    }

    private static Map<String, GuildItemToggleDefinition> defaultToggleLibrary() {
        Map<String, GuildItemToggleDefinition> toggles = new LinkedHashMap<>();

        GuildItemToggleDefinition toggle = new GuildItemToggleDefinition();
        toggle.all = new GuildItemDefinition("LIME_DYE");
        toggle.all.name = "<green><bold>Pokaż wszystkie";
        toggle.all.lore = new ArrayList<>(List.of("<gray>Kliknij aby pokazać tylko brakujące itemy"));
        toggle.missingOnly = new GuildItemDefinition("RED_DYE");
        toggle.missingOnly.name = "<red><bold>Tylko brakujące";
        toggle.missingOnly.lore = new ArrayList<>(List.of("<gray>Kliknij aby pokazać wszystkie itemy"));
        toggles.put("item-t", toggle);

        return toggles;
    }

    private static Map<String, GuildItemSet> defaultSets() {
        Map<String, GuildItemSet> sets = new LinkedHashMap<>();

        GuildItemSet defaultSet = new GuildItemSet();
        defaultSet.priority = 0;
        defaultSet.items = new LinkedHashMap<>(Map.of("diamonds", 64, "emeralds", 64, "iron", 64));
        defaultSet.requiredLevel = 30;
        defaultSet.requiredMoney = 10000.0;
        defaultSet.requiredRank = 1000;
        sets.put("default", defaultSet);

        GuildItemSet vip = new GuildItemSet();
        vip.permission = "funnyguilds.items.vip";
        vip.priority = 10;
        vip.requirements = new SetRequirements();
        vip.requirements.moneyEnabled = false;
        vip.items = new LinkedHashMap<>(Map.of("diamonds", 32, "emeralds", 32, "iron", 32));
        vip.requiredLevel = 20;
        vip.requiredRank = 800;
        sets.put("vip", vip);

        return sets;
    }

}

