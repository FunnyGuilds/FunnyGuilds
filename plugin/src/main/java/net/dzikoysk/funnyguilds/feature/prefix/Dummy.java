package net.dzikoysk.funnyguilds.feature.prefix;

import java.text.MessageFormat;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import panda.std.Option;

public class Dummy {

    private static final String OBJECTIVE_NAME = "FG-Points";
    private final User user;

    public Dummy(User user) {
        this.user = user;
        this.initialize();
    }

    public void updateScore(User user) {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().dummyEnable) {
            return;
        }

        if (user == null) {
            return;
        }

        if (user.hasPermission("funnyguilds.admin.disabledummy")) {
            return;
        }

        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to update Dummy score but scoreboard hasn't been initialized yet " +
                    "(maybe player left the game while updating?)");
            return;
        }

        scoreboardOption
                .map(scoreboard -> scoreboard.getObjective(OBJECTIVE_NAME))
                .filter(objective -> objective.getName().equals(OBJECTIVE_NAME))
                .peek(objective -> objective.getScore(user.getName()).setScore(user.getRank().getPoints()))
                .onEmpty(this::initialize);
    }

    @SuppressWarnings("deprecation")
    private void initialize() {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        if (!plugin.getPluginConfiguration().dummyEnable) {
            return;
        }

        if (this.user.hasPermission("funnyguilds.admin.disabledummy")) {
            return;
        }

        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to initialize Dummy, but we haven't initialized scoreboard yet " +
                            "(maybe player left the game while initializing?)");
            return;
        }

        Scoreboard scoreboard = scoreboardOption.get();
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);

        if (objective == null || !objective.getName().equals(OBJECTIVE_NAME)) {
            objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(plugin.getPluginConfiguration().dummySuffix.getValue());
        }

        UserManager userManager = plugin.getUserManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Option<User> userOption = userManager.findByPlayer(player);

            if (userOption.isEmpty()) {
                FunnyGuilds.getPluginLogger().debug(MessageFormat.format(
                        "Online player named: {0} does not have corresponding user instance while initializing Dummy for user: {1}",
                        player.getName(), this.user.getName()));
                continue;
            }

            User user = userOption.get();
            objective.getScore(user.getName()).setScore(user.getRank().getPoints());
        }
    }

}
