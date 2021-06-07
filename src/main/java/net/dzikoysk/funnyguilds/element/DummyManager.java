package net.dzikoysk.funnyguilds.element;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DummyManager {

    private final UserManager userManager;

    public DummyManager(FunnyGuilds plugin) {
        this.userManager = plugin.getUserManager();
    }

    public void updatePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScore(userManager.getUser(player));
        }
    }

    public void updateScore(User user) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            userManager.getUser(player).getCache().getDummy().updateScore(user);
        }
    }
}
