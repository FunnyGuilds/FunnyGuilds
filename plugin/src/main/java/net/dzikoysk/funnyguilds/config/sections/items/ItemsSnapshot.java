package net.dzikoysk.funnyguilds.config.sections.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Immutable runtime view of {@code items.yml}, published by {@link ItemsConfiguration#snapshot()}
 * only after a successful validation. Readers capture one snapshot per operation and never see
 * a mix of versions during an asynchronous reload.
 */
public final class ItemsSnapshot {

    private final Map<String, GuildItemDefinition> guildItemLibrary;
    private final Map<String, GuildItemToggleDefinition> guildToggleLibrary;
    private final Map<String, GuildItemSet> guildItemSets;
    private final String adminItemsBypassPermission;
    private final GuiConfiguration gui;

    public ItemsSnapshot(
            Map<String, GuildItemDefinition> guildItemLibrary,
            Map<String, GuildItemToggleDefinition> guildToggleLibrary,
            Map<String, GuildItemSet> guildItemSets,
            String adminItemsBypassPermission,
            GuiConfiguration gui
    ) {
        this.guildItemLibrary = copyMap(guildItemLibrary);
        this.guildToggleLibrary = copyMap(guildToggleLibrary);
        this.guildItemSets = copyMap(guildItemSets);
        this.adminItemsBypassPermission = adminItemsBypassPermission;
        this.gui = gui;
    }

    static ItemsSnapshot of(ItemsConfiguration config) {
        return new ItemsSnapshot(
                config.guildItemLibrary,
                config.guildToggleLibrary,
                config.guildItemSets,
                config.adminItemsBypassPermission,
                config.gui
        );
    }

    private static <K, V> Map<K, V> copyMap(Map<K, V> map) {
        if (map == null) {
            return Map.of();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    public Map<String, GuildItemDefinition> getGuildItemLibrary() {
        return this.guildItemLibrary;
    }

    public Map<String, GuildItemToggleDefinition> getGuildToggleLibrary() {
        return this.guildToggleLibrary;
    }

    public Map<String, GuildItemSet> getGuildItemSets() {
        return this.guildItemSets;
    }

    public String getAdminItemsBypassPermission() {
        return this.adminItemsBypassPermission;
    }

    public GuiConfiguration getGui() {
        return this.gui;
    }

    /**
     * Pobiera definicję itemu z library po kluczu.
     * Sprawdza najpierw zwykłe itemy, potem toggle (zwraca stan "all").
     */
    public Optional<GuildItemDefinition> getLibraryItem(String key) {
        GuildItemDefinition direct = this.guildItemLibrary.get(key);
        if (direct != null) {
            return Optional.of(direct);
        }
        GuildItemToggleDefinition toggle = this.guildToggleLibrary.get(key);
        if (toggle != null) {
            return Optional.ofNullable(toggle.all);
        }
        return Optional.empty();
    }

    /**
     * Pobiera definicję toggle itemu z library.
     */
    public Optional<GuildItemToggleDefinition> getToggleItem(String key) {
        return Optional.ofNullable(this.guildToggleLibrary.get(key));
    }

    /**
     * Rozwiązuje item — najpierw sprawdza per-set overrides, potem library.
     */
    public Optional<GuildItemDefinition> resolveItem(String key, PerSetConfig perSet) {
        if (perSet != null && perSet.libraryOverrides != null) {
            GuildItemDefinition override = perSet.libraryOverrides.get(key);
            if (override != null) {
                return Optional.of(override);
            }
        }
        return getLibraryItem(key);
    }

    /**
     * Zwraca sety posortowane malejąco po priority.
     */
    public List<GuildItemSet> getSetsSortedByPriority() {
        List<GuildItemSet> sorted = new ArrayList<>(this.guildItemSets.values());
        sorted.sort(Comparator.comparingInt((GuildItemSet set) -> set.priority).reversed());
        return List.copyOf(sorted);
    }

    public Optional<GuildItemSet> findGuildItemSet(String name, boolean ignoreCase) {
        if (this.guildItemSets.isEmpty()) {
            return Optional.empty();
        }

        GuildItemSet exactSet = this.guildItemSets.get(name);
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

}
