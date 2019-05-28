package net.dzikoysk.funnyguilds.element;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Dummy {

    private static final String OBJECTIVE_NAME = "FG-Points";
    private final        User   user;

    public Dummy(User user) {
        this.user = user;
        this.initialize();
    }

    public void updateScore(User user) {
        if (! FunnyGuilds.getInstance().getPluginConfiguration().dummyEnable) {
            return;
        }

        Scoreboard scoreboard = this.user.getCache().getScoreboard();

        if (scoreboard == null) {
            FunnyGuilds.getInstance().getPluginLogger().debug(
                    "We're trying to update Dummy score but scoreboard hasn't been initialized yet" +
                            "(maybe player left the game while updating?)");
            return;
        }

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);

        if (objective == null || ! objective.getName().equals(OBJECTIVE_NAME)) {
            this.initialize();
        } else {
            objective.getScore(user.getName()).setScore(user.getRank().getPoints());
        }
    }

    private void initialize() {
        if (! FunnyGuilds.getInstance().getPluginConfiguration().dummyEnable) {
            return;
        }
        
        Scoreboard scoreboard = this.user.getCache().getScoreboard();

        if (scoreboard == null) {
            FunnyGuilds.getInstance().getPluginLogger().debug(
                    "We're trying to initialize Dummy, but we haven't initialized scoreboard yet " +
                            "(maybe player left the game while initializing?)");
            return;
        }

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);

        if (objective == null || ! objective.getName().equals(OBJECTIVE_NAME)) {
            objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(FunnyGuilds.getInstance().getPluginConfiguration().dummySuffix);
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = User.get(player);
            objective.getScore(user.getName()).setScore(user.getRank().getPoints());
        }
    }

}
