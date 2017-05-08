package net.dzikoysk.funnyguilds.command.util;

import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface Executor {

    void execute(CommandSender sender, String[] args);

    default boolean checkWorld(Player player) {
        PluginConfig settings = Settings.getConfig(); // TODO
        List<String> blockedWorlds = settings.blockedWorlds;
        World playerWorld = player.getWorld();
        return blockedWorlds != null && blockedWorlds.size() == 0 && blockedWorlds.contains(playerWorld.getName());
    }

}
