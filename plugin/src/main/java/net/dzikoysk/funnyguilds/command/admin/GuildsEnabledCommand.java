package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
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
