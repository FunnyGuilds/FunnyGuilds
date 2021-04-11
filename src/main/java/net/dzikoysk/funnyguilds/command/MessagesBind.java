package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.panda_lang.utilities.inject.Resources;

@FunnyComponent
final class MessagesBind implements Bind {

    @Override
    public void accept(Resources injectorResources) {
        injectorResources.on(MessageConfiguration.class).assignInstance(() -> FunnyGuilds.getInstance().getMessageConfiguration());
    }

}
