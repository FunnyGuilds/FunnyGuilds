package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.panda_lang.utilities.inject.Resources;

@FunnyComponent
final class SettingsBind implements Bind {

    @Override
    public void accept(Resources injectorResources) {
        injectorResources.on(PluginConfiguration.class).assignInstance(() -> FunnyGuilds.getInstance().getPluginConfiguration());
    }

}

