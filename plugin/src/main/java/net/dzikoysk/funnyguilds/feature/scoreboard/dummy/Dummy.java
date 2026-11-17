package net.dzikoysk.funnyguilds.feature.scoreboard.dummy;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import java.util.Optional;
import org.bukkit.scoreboard.Scoreboard;

public class Dummy {

    private static final String OBJECTIVE_NAME = "FG-Points";

    private final PluginConfiguration pluginConfiguration;
    private final User user;

    Dummy(PluginConfiguration pluginConfiguration, User user) {
        this.pluginConfiguration = pluginConfiguration;
        this.user = user;
    }

    @SuppressWarnings("deprecation")
    Optional<Objective> initialize() {
        if (this.user.hasPermission("funnyguilds.admin.disabledummy")) {
            return Optional.empty();
        }

        Optional<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to initialize Dummy, but scoreboard hasn't been initialized.");
            return Optional.empty();
        }
        Scoreboard scoreboard = scoreboardOption.orElseThrow();

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective != null) {
            return Optional.of(objective);
        }

        objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.displayName(this.pluginConfiguration.scoreboard.dummy.suffix);

        return Optional.of(objective);
    }

    // Update specific player for this user
    public void updatePlayer(User target) {
        if (target.hasPermission("funnyguilds.admin.disabledummy")) {
            return;
        }

        Optional<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to update Dummy score, but scoreboard hasn't been initialized.");
            return;
        }
        scoreboardOption
                .map(scoreboard -> scoreboard.getObjective(OBJECTIVE_NAME))
                .or(this::initialize)
                .ifPresent(objective -> objective.getScore(target.getName()).setScore(target.getRank().getPoints()));
    }

}
