package net.dzikoysk.funnyguilds.command.util;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.data.Settings;

public interface Executor {

    void execute(CommandSender sender, String[] args);

    default boolean checkWorld(Player player) {
        List<String> blockedWorlds = Settings.getConfig().blockedWorlds;
        return (blockedWorlds != null) && (!blockedWorlds.isEmpty()) && blockedWorlds.contains(player.getWorld().getName());
    }
}