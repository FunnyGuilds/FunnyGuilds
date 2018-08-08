package net.dzikoysk.funnyguilds.element;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Dummy {

    private static final String name = "FG-Points";
    private final User user;

    public Dummy(User user) {
        this.user = user;
        this.initialize();
    }

    public void updateScore(User user) {
        if (!Settings.getConfig().dummyEnable) {
            return;
        }
        
        Objective objective = this.user.getCache().getScoreboard().getObjective(name);
        if (objective == null || !objective.getName().equals(name)) {
            initialize();
        } else {
            objective.getScore(user.getName()).setScore(user.getRank().getPoints());
        }
    }

    private void initialize() {
        if (!Settings.getConfig().dummyEnable) {
            return;
        }
        
        Scoreboard scoreboard = this.user.getCache().getScoreboard();
        Objective objective = scoreboard.getObjective(name);
        
        if (objective == null || !objective.getName().equals(name)) {
            objective = scoreboard.registerNewObjective(name, "dummy");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(Settings.getConfig().dummySuffix);
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = User.get(player);
            objective.getScore(user.getName()).setScore(user.getRank().getPoints());
        }
    }

}
