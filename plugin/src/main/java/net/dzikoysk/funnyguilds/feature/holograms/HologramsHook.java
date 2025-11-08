package net.dzikoysk.funnyguilds.feature.holograms;

import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.jetbrains.annotations.NotNull;

public abstract class HologramsHook extends AbstractPluginHook {

    protected HologramsHook(String name) {
        super(name);
    }

    public abstract void update(@NotNull Guild guild);
}
