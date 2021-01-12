package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.InjectorResources;

@FunnyComponent
final class PlayerBind implements Bind {

    @Override
    public void accept(InjectorResources injectorResources) {
        injectorResources.on(Player.class).assignHandler((injectorProperty, annotation, args) -> {
            Origin origin = CommandUtils.getOrigin(args);
            CommandSender commandSender = origin.getCommandSender();

            if (!(commandSender instanceof Player)) {
                throw new IllegalStateException("Cannot use player bind in non-player command");
            }

            return commandSender;
        });
    }
}
