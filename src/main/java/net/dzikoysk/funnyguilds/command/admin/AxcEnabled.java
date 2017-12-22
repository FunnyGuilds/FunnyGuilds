package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import org.bukkit.command.CommandSender;

public class AxcEnabled implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();

        boolean enabled = config.guildsEnabled = !config.guildsEnabled;
        sender.sendMessage(enabled ? messages.adminGuildsEnabled : messages.adminGuildsDisabled);
    }

}
