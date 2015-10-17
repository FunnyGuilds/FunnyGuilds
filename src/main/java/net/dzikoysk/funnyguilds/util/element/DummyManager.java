package net.dzikoysk.funnyguilds.util.element;

import net.dzikoysk.funnyguilds.basic.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DummyManager {

    public static void updatePlayers() {
        for (Player player : Bukkit.getOnlinePlayers())
            updateScore(User.get(player));
    }

    public static void updateScore(User user) {
        for (Player player : Bukkit.getOnlinePlayers())
            User.get(player).getDummy().updateScore(user);
    }
}
