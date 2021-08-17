package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import org.bukkit.command.CommandSender;

public final class GuildsEnabledCommand {

    @FunnyCommand(
        name = "${admin.guilds-enabled.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, CommandSender sender) {
        config.guildsEnabled = !config.guildsEnabled;
        sender.sendMessage(config.guildsEnabled ? messages.adminGuildsEnabled : messages.adminGuildsDisabled);
    }

}
