package net.dzikoysk.funnyguilds.feature.prefix;

import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DummyManager {

    public static void updatePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScore(UserUtils.get(player.getUniqueId()));
        }
    }

    public static void updateScore(User user) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UserUtils.get(player.getUniqueId()).getCache().getDummy().updateScore(user);
        }
    }
}
