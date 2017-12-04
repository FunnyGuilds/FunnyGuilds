package net.dzikoysk.funnyguilds.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;

public class ExcItems implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PluginConfig config = Settings.getConfig();

        if (!config.useCommonGUI && player.hasPermission("funnyguilds.vip.items")) {
            config.guiItemsVip.open(player);
            return;
        }
        
        config.guiItems.open(player);
    }
}
