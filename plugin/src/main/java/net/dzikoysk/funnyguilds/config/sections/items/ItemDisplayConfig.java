package net.dzikoysk.funnyguilds.config.sections.items;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * Konfiguracja wyświetlania itemów w GUI — nakładka na itemy z library.
 * Dwa warianty: {@code has-enough} i {@code missing}.
 */
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class ItemDisplayConfig extends OkaeriConfig {

    /** Czy item ma mieć efekt "glow" (enchant + HIDE_ENCHANTS). */
    public boolean glow = false;

    /** Prefix dodawany do nazwy itemu (np. {@code <green>✔ } lub {@code <red>✘ }). */
    @Nullable
    public String namePrefix;

    /** Dodatkowe linie lore z placeholderami ({REQUIRED}, {INV}, {ENDER}, {TOTAL}, {MISSING}). */
    public List<String> additionalLore = new ArrayList<>();

    public ItemDisplayConfig() {
    }

    public boolean hasAdditionalLore() {
        return additionalLore != null && !additionalLore.isEmpty();
    }

}

