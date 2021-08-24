package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.Resources;

@FunnyComponent
final class UserBind implements Bind {

    @Override
    public void accept(Resources injectorResources) {
        injectorResources.on(User.class).assignHandler((property, annotation, args) -> fetchUser(CommandUtils.getContext(args)));
    }

    public User fetchUser(Context context) {
        CommandSender commandSender = context.getCommandSender();

        if (!(commandSender instanceof OfflinePlayer)) {
            throw new IllegalStateException("Cannot use user bind in non-player command");
        }

        return UserUtils.get(((OfflinePlayer) commandSender).getUniqueId());
    }

}
