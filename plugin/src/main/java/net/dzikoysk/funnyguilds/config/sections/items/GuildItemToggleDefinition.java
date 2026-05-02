package net.dzikoysk.funnyguilds.config.sections.items;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;

/**
 * Definicja itemu z dwoma stanami — używana dla przycisków toggle w GUI.
 * Klucz w library: np. {@code item-t}.
 */
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class GuildItemToggleDefinition extends OkaeriConfig {

    /** Stan "pokaż wszystkie". */
    public GuildItemDefinition all = new GuildItemDefinition("LIME_DYE");

    /** Stan "pokaż tylko brakujące". */
    public GuildItemDefinition missingOnly = new GuildItemDefinition("RED_DYE");

    public GuildItemToggleDefinition() {
    }

}

