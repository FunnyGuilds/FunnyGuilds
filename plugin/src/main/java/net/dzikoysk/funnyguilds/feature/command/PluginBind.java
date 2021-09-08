package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.panda_lang.utilities.inject.Resources;

@FunnyComponent
final class PluginBind implements Bind {

    @Override
    public void accept(Resources injectorResources) {
        injectorResources.on(FunnyGuilds.class).assignHandler((property, annotation, args) -> FunnyGuilds.getInstance());
    }

}
