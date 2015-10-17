package net.dzikoysk.funnyguilds.util.element;

import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Dummy {

    private static final String NAME = "points";
    private final User user;

    public Dummy(User user) {
        this.user = user;
        this.initialize();
    }

    @SuppressWarnings("deprecation")
    public void updateScore(User user) {
        if (!Settings.getInstance().dummyEnable)
            return;
        Scoreboard scoreboard = this.user.getScoreboard();
        Objective objective = scoreboard.getObjective(NAME);
        if (objective == null || !objective.getName().equals(NAME))
            initialize();
        else {
            OfflineUser offline = user.getOfflineUser();
            objective.getScore(offline).setScore(user.getRank().getPoints());
        }
    }

    private void initialize() {
        if (!Settings.getInstance().dummyEnable)
            return;
        Scoreboard scoreboard = this.user.getScoreboard();
        Objective objective = scoreboard.getObjective(NAME);
        if (objective == null || !objective.getName().equals(NAME)) {
            objective = scoreboard.registerNewObjective(NAME, "dummy");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(Settings.getInstance().dummySuffix);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = User.get(player);
            @SuppressWarnings("deprecation")
            Score score = objective.getScore(user.getOfflineUser());
            score.setScore(user.getRank().getPoints());
        }
    }
}
