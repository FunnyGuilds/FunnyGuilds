package net.dzikoysk.funnyguilds.command.util;

import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface Executor {

    void execute(CommandSender sender, String[] args);

    default boolean checkWorld(Player player) {
        List<String> blockedWorlds = Settings.getConfig().blockedWorlds;
        return blockedWorlds != null && blockedWorlds.size() > 0 && blockedWorlds.contains(player.getWorld().getName());
    }
}