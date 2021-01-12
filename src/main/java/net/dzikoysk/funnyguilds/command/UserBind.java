package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.commons.function.TriFunction;
import org.panda_lang.utilities.inject.InjectorProperty;
import org.panda_lang.utilities.inject.InjectorResources;

@FunnyComponent
final class UserBind implements Bind, TriFunction<InjectorProperty, Object, Object[], User> {

    @Override
    public void accept(InjectorResources injectorResources) {
        injectorResources.on(User.class).assignHandler(this::apply);
    }

    @Override
    public User apply(InjectorProperty injectorProperty, Object annotation, Object[] args) {
        Origin origin = CommandUtils.getOrigin(args);
        CommandSender commandSender = origin.getCommandSender();

        if (!(commandSender instanceof OfflinePlayer)) {
            throw new IllegalStateException("Cannot use user bind in non-player command");
        }

        return User.get((OfflinePlayer) commandSender);
    }

}
