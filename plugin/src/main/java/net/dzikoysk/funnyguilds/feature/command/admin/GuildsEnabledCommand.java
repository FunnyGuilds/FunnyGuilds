package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import org.bukkit.command.CommandSender;

public final class GuildsEnabledCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.guilds-enabled.name}",
            permission = "funnyguilds.admin",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        config.guildsEnabled = !config.guildsEnabled;
        sender.sendMessage(config.guildsEnabled ? messages.adminGuildsEnabled : messages.adminGuildsDisabled);
    }

}
