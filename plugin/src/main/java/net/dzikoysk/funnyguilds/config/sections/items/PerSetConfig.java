package net.dzikoysk.funnyguilds.config.sections.items;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Nadpisania konfiguracji per-set — pozwala nadpisać tytuł GUI i itemy z library dla konkretnego setu.
 */
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PerSetConfig extends OkaeriConfig {

    /** Nadpisany tytuł GUI dla tego setu. {@code null} = używa globalnego tytułu. */
    @Nullable
    public String title;

    /** Nadpisania itemów z library — klucz → nadpisana definicja. */
    public Map<String, GuildItemDefinition> libraryOverrides = new LinkedHashMap<>();

    public PerSetConfig() {
    }

    public boolean hasTitle() {
        return title != null && !title.isEmpty();
    }

}

