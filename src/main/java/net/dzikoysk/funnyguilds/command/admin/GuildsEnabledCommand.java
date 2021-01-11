package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.bukkit.command.CommandSender;

public final class GuildsEnabledCommand {

    @FunnyCommand(
        name = "${admin.guilds-enabled.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        boolean enabled = config.guildsEnabled = !config.guildsEnabled;
        sender.sendMessage(enabled ? messages.adminGuildsEnabled : messages.adminGuildsDisabled);
    }

}
