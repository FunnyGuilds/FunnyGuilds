package net.dzikoysk.funnyguilds.element;

import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DummyManager {

    public static void updatePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScore(User.get(player));
        }
    }

    public static void updateScore(User user) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User.get(player).getCache().getDummy().updateScore(user);
        }
    }
}
