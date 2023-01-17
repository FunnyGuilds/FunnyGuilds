package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.panda_lang.utilities.inject.Resources;

@FunnyComponent
final class GuildBind implements Bind {

    private final MessageConfiguration messages;
    private final UserBind userBind;

    GuildBind(MessageConfiguration messages, UserManager userManager) {
        this.messages = messages;
        this.userBind = new UserBind(userManager);
    }

    @Override
    public void accept(Resources injectorResources) {
        injectorResources.on(Guild.class).assignHandler((property, annotation, args) ->
                this.userBind.fetchUser(CommandUtils.getContext(args))
                        .getGuild()
                        .orThrow(() -> new InternalValidationException(config -> config.generalHasNoGuild)));
    }

}
