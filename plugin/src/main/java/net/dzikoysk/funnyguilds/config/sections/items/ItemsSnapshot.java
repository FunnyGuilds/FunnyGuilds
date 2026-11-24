package net.dzikoysk.funnyguilds.config.sections.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.shared.bukkit.ResolvedItemDefinition;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable runtime view of {@code items.yml}, published by {@link ItemsConfiguration#snapshot()}
 * only after a successful validation. Values are deep copies detached from the live configuration:
 * readers capture one snapshot per operation and neither an asynchronous reload nor mutation of the
 * source objects can change an already captured version, and published values cannot be mutated.
 */
public final class ItemsSnapshot {

    private final Map<String, ItemDefinition> guildItemLibrary;
    private final Map<String, ToggleDefinition> guildToggleLibrary;
    private final Map<String, ItemSet> guildItemSets;
    private final String adminItemsBypassPermission;
    private final Gui gui;

    public ItemsSnapshot(
            Map<String, GuildItemDefinition> guildItemLibrary,
            Map<String, GuildItemToggleDefinition> guildToggleLibrary,
            Map<String, GuildItemSet> guildItemSets,
            String adminItemsBypassPermission,
            GuiConfiguration gui
    ) {
        this.guildItemLibrary = copyValues(guildItemLibrary, ItemDefinition::new);
        this.guildToggleLibrary = copyValues(guildToggleLibrary, ToggleDefinition::new);
        this.guildItemSets = copyValues(guildItemSets, ItemSet::new);
        this.adminItemsBypassPermission = adminItemsBypassPermission;
        this.gui = gui != null ? new Gui(gui) : null;
    }

    /** Captures a deeply immutable copy of the current configuration values. */
    public static ItemsSnapshot of(ItemsConfiguration config) {
        return new ItemsSnapshot(
                config.guildItemLibrary,
                config.guildToggleLibrary,
                config.guildItemSets,
                config.adminItemsBypassPermission,
                config.gui
        );
    }

    private static <K, S, T> Map<K, T> copyValues(Map<K, S> source, Function<S, T> copier) {
        if (source == null) {
            return Map.of();
        }
        // Keep insertion order: set selection and GUI slots depend on the configured YAML order.
        Map<K, T> copy = new LinkedHashMap<>();
        source.forEach((key, value) -> copy.put(key, value != null ? copier.apply(value) : null));
        return Collections.unmodifiableMap(copy);
    }

    private static <E> List<E> copyList(@Nullable List<E> source) {
        // Null elements are legal in lore (rendered as blank lines), so reject nothing here.
        return source != null ? Collections.unmodifiableList(new ArrayList<>(source)) : null;
    }

    public Map<String, ItemDefinition> getGuildItemLibrary() {
        return this.guildItemLibrary;
    }

    public Map<String, ToggleDefinition> getGuildToggleLibrary() {
        return this.guildToggleLibrary;
    }

    public Map<String, ItemSet> getGuildItemSets() {
        return this.guildItemSets;
    }

    public String getAdminItemsBypassPermission() {
        return this.adminItemsBypassPermission;
    }

    public Gui getGui() {
        return this.gui;
    }

    /**
     * Pobiera definicję itemu z library po kluczu.
     * Sprawdza najpierw zwykłe itemy, potem toggle (zwraca stan "all").
     */
    public Optional<ItemDefinition> getLibraryItem(String key) {
        ItemDefinition direct = this.guildItemLibrary.get(key);
        if (direct != null) {
            return Optional.of(direct);
        }
        ToggleDefinition toggle = this.guildToggleLibrary.get(key);
        if (toggle != null) {
            return Optional.ofNullable(toggle.all);
        }
        return Optional.empty();
    }

    /**
     * Pobiera definicję toggle itemu z library.
     */
    public Optional<ToggleDefinition> getToggleItem(String key) {
        return Optional.ofNullable(this.guildToggleLibrary.get(key));
    }

    /**
     * Rozwiązuje item — najpierw sprawdza per-set overrides, potem library.
     */
    public Optional<ItemDefinition> resolveItem(String key, PerSet perSet) {
        if (perSet != null) {
            ItemDefinition override = perSet.libraryOverrides.get(key);
            if (override != null) {
                return Optional.of(override);
            }
        }
        return getLibraryItem(key);
    }

    /**
     * Zwraca sety posortowane malejąco po priority.
     */
    public List<ItemSet> getSetsSortedByPriority() {
        List<ItemSet> sorted = new ArrayList<>(this.guildItemSets.values());
        sorted.sort(Comparator.comparingInt((ItemSet set) -> set.priority).reversed());
        return List.copyOf(sorted);
    }

    public Optional<ItemSet> findGuildItemSet(String name, boolean ignoreCase) {
        if (this.guildItemSets.isEmpty()) {
            return Optional.empty();
        }

        ItemSet exactSet = this.guildItemSets.get(name);
        if (exactSet != null) {
            return Optional.of(exactSet);
        }

        if (ignoreCase) {
            return this.guildItemSets.entrySet().stream()
                    .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                    .map(Map.Entry::getValue)
                    .findFirst();
        }

        return Optional.empty();
    }

    /** Immutable runtime values of a single guild item definition. */
    public static final class ItemDefinition {

        private final ResolvedItemDefinition resolved;

        public final @Nullable String material;
        public final @Nullable String name;
        public final @Nullable List<String> lore;
        public final @Nullable List<String> enchants;
        public final @Nullable List<String> flags;
        public final @Nullable Integer customModelData;
        public final @Nullable String skullOwner;
        public final @Nullable String armorColor;

        public ItemDefinition(GuildItemDefinition definition) {
            this.material = definition.material;
            this.name = definition.name;
            this.lore = copyList(definition.lore);
            this.enchants = copyList(definition.enchants);
            this.flags = copyList(definition.flags);
            this.customModelData = definition.customModelData;
            this.skullOwner = definition.skullOwner;
            this.armorColor = definition.armorColor;
            this.resolved = definition.getResolved();
        }

        /** Loaded definitions are resolved eagerly at snapshot creation. */
        public ResolvedItemDefinition getResolved() {
            return this.resolved;
        }

    }

    /** Immutable runtime values of a toggle item with its two states. */
    public static final class ToggleDefinition {

        public final ItemDefinition all;
        public final ItemDefinition missingOnly;

        public ToggleDefinition(GuildItemToggleDefinition toggle) {
            this.all = toggle.all != null ? new ItemDefinition(toggle.all) : null;
            this.missingOnly = toggle.missingOnly != null ? new ItemDefinition(toggle.missingOnly) : null;
        }

    }

    /** Immutable runtime values of the requirement flags of a set. */
    public static final class Requirements {

        public final boolean itemsEnabled;
        public final boolean levelEnabled;
        public final boolean moneyEnabled;
        public final boolean rankEnabled;

        public Requirements(SetRequirements requirements) {
            this.itemsEnabled = requirements != null && requirements.itemsEnabled;
            this.levelEnabled = requirements != null && requirements.levelEnabled;
            this.moneyEnabled = requirements != null && requirements.moneyEnabled;
            this.rankEnabled = requirements != null && requirements.rankEnabled;
        }

    }

    /** Immutable runtime values of a single guild item set. */
    public static final class ItemSet {

        public final @Nullable String permission;
        public final int priority;
        public final Requirements requirements;
        public final Map<String, Integer> items;
        public final int requiredLevel;
        public final double requiredMoney;
        public final int requiredRank;

        public ItemSet(GuildItemSet set) {
            this.permission = set.permission;
            this.priority = set.priority;
            this.requirements = new Requirements(set.requirements);
            this.items = set.items != null ? Collections.unmodifiableMap(new LinkedHashMap<>(set.items)) : Map.of();
            this.requiredLevel = set.requiredLevel;
            this.requiredMoney = set.requiredMoney;
            this.requiredRank = set.requiredRank;
        }

        public Map<String, Integer> getItems() {
            return this.items;
        }

    }

    /** Immutable runtime values of the display overlay of GUI items. */
    public static final class DisplayConfig {

        public final boolean glow;
        public final @Nullable String namePrefix;
        public final @Nullable List<String> additionalLore;

        public DisplayConfig(ItemDisplayConfig display) {
            this.glow = display.glow;
            this.namePrefix = display.namePrefix;
            this.additionalLore = copyList(display.additionalLore);
        }

        public boolean hasAdditionalLore() {
            return this.additionalLore != null && !this.additionalLore.isEmpty();
        }

    }

    /** Immutable runtime values of the two display variants (has-enough / missing). */
    public static final class DisplayGroup {

        public final DisplayConfig hasEnough;
        public final DisplayConfig missing;

        public DisplayGroup(GuiConfiguration.ItemDisplayGroup group) {
            this.hasEnough = group != null && group.hasEnough != null ? new DisplayConfig(group.hasEnough) : null;
            this.missing = group != null && group.missing != null ? new DisplayConfig(group.missing) : null;
        }

    }

    /** Immutable runtime values of the per-set GUI overrides. */
    public static final class PerSet {

        private static final PerSet EMPTY = new PerSet(null, new LinkedHashMap<>());

        public final @Nullable String title;
        public final Map<String, ItemDefinition> libraryOverrides;

        public PerSet(@Nullable String title, Map<String, ItemDefinition> libraryOverrides) {
            this.title = title;
            this.libraryOverrides = Collections.unmodifiableMap(new LinkedHashMap<>(libraryOverrides));
        }

        private static PerSet copy(PerSetConfig perSet) {
            if (perSet == null) {
                return EMPTY;
            }
            return new PerSet(
                    perSet.title,
                    perSet.libraryOverrides != null
                            ? copyValues(perSet.libraryOverrides, ItemDefinition::new)
                            : Map.of()
            );
        }

        public boolean hasTitle() {
            return this.title != null && !this.title.isEmpty();
        }

    }

    /** Immutable runtime values of the GUI section. */
    public static final class Gui {

        public final boolean enabled;
        public final @Nullable String title;
        public final int rows;
        public final List<String> pattern;
        public final DisplayGroup itemDisplay;
        public final Map<String, PerSet> perSet;

        public Gui(GuiConfiguration gui) {
            this.enabled = gui.enabled;
            this.title = gui.title;
            this.rows = gui.rows;
            this.pattern = gui.pattern != null
                    ? Collections.unmodifiableList(new ArrayList<>(gui.pattern))
                    : List.of();
            this.itemDisplay = gui.itemDisplay != null ? new DisplayGroup(gui.itemDisplay) : null;
            if (gui.perSet != null) {
                Map<String, PerSet> perSet = new LinkedHashMap<>();
                gui.perSet.forEach((key, value) -> perSet.put(key, PerSet.copy(value)));
                this.perSet = Collections.unmodifiableMap(perSet);
            }
            else {
                this.perSet = Map.of();
            }
        }

        public PerSet getPerSetConfig(String setName) {
            PerSet perSet = this.perSet.get(setName);
            return perSet != null ? perSet : new PerSet(null, new LinkedHashMap<>());
        }

    }

}
