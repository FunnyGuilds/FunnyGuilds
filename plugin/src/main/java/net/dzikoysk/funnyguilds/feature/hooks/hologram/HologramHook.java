package net.dzikoysk.funnyguilds.feature.hooks.hologram;

import net.dzikoysk.funnyguilds.feature.hologram.FunnyHologramManager;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;

public abstract class HologramHook extends AbstractPluginHook implements FunnyHologramManager {

    protected HologramHook(String name) {
        super(name);
    }

}
