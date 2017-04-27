package net.dzikoysk.funnyguilds.command.util;

import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface Executor {

    void execute(CommandSender sender, String[] args);

    default boolean checkWorld(Player player) {
        Settings settings = Settings.getInstance(); // TODO
        List<String> blockedWorlds = settings.blockedWorlds;
        if (blockedWorlds == null || blockedWorlds.size() == 0) {
            return true;
        }
        World playerWorld = player.getWorld();
        for (String blockedWorld : blockedWorlds) {
            if (playerWorld.getName().equals(blockedWorld)) {
                return false;
            }
        }
        return true;
    }

}
