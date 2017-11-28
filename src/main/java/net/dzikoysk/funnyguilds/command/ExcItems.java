package net.dzikoysk.funnyguilds.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;

public class ExcItems implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        PluginConfig c = Settings.getConfig();

        if (!c.useCommonGUI && p.hasPermission("funnyguilds.vip")) {
            c.guiItemsVip.open(p);
            return;
        }
        
        c.guiItems.open(p);
    }
}
