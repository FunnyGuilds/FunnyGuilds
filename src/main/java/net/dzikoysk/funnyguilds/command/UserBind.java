package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.Resources;

@FunnyComponent
final class UserBind implements Bind {

    private final FunnyGuilds plugin;

    UserBind(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public void accept(Resources injectorResources) {
        injectorResources.on(User.class).assignHandler((property, annotation, args) -> fetchUser(CommandUtils.getContext(args)));
    }

    public User fetchUser(Context context) {
        CommandSender commandSender = context.getCommandSender();

        if (!(commandSender instanceof OfflinePlayer)) {
            throw new IllegalStateException("Cannot use user bind in non-player command");
        }

        return plugin.getUserManager().getUser((OfflinePlayer) commandSender);
    }

}
