package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import org.bukkit.command.CommandSender;

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
