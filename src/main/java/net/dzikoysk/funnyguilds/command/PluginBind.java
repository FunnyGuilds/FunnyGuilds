package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.panda_lang.utilities.inject.Resources;

@FunnyComponent
final class PluginBind implements Bind {

    private final FunnyGuilds plugin;

    public PluginBind(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public void accept(Resources injectorResources) {
        injectorResources.on(FunnyGuilds.class).assignInstance(plugin);
    }

}

