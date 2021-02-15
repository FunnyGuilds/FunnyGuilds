package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import net.dzikoysk.funnyguilds.command.UserValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SpyCommand {

    @FunnyCommand(
        name = "${admin.spy.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender) {
        UserCache cache = UserValidation.requireUserByName(sender.getName()).getCache();

        if (cache.isSpy()) {
            cache.setSpy(false);
            sender.sendMessage(messages.adminStopSpy);
        }
        else {
            cache.setSpy(true);
            sender.sendMessage(messages.adminStartSpy);
        }
    }

}
