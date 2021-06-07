package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.panda_lang.utilities.inject.Resources;

@FunnyComponent
final class GuildBind implements Bind {

    private static final UserBind USER_BIND = new UserBind(FunnyGuilds.getInstance());

    @Override
    public void accept(Resources injectorResources) {
        injectorResources.on(Guild.class).assignHandler((property, annotation, args) -> {
            User user = USER_BIND.fetchUser(CommandUtils.getContext(args));

            if (!user.hasGuild()) {
                throw new ValidationException(FunnyGuilds.getInstance().getMessageConfiguration().generalHasNoGuild);
            }

            return user.getGuild();
        });
    }

}
