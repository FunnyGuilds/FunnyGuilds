package net.dzikoysk.funnyguilds.config.sections.items;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;

/**
 * Flagi wymagań per set — określają, które wymagania są aktywne.
 */
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class SetRequirements extends OkaeriConfig {

    public boolean itemsEnabled = true;

    public boolean experienceEnabled = true;

    public boolean moneyEnabled = true;

    public boolean rankEnabled = true;

    public SetRequirements() {
    }

}

