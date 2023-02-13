package net.dzikoysk.funnyguilds.feature.scoreboard;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardService {

    private final PluginConfiguration pluginConfiguration;

    public ScoreboardService(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    public void updatePlayer(Player player, User user) {
        if (!this.pluginConfiguration.scoreboard.enabled) {
            return;
        }

        if (player == null) {
            return;
        }

        UserCache cache = user.getCache();
        cache.getScoreboard().peek(scoreboard -> {
            try {
                player.setScoreboard(scoreboard);
            }
            catch (IllegalStateException e) {
                FunnyGuilds.getPluginLogger().error("[ScoreboardService] Cannot set scoreboard for " + user.getName(), e);
            }
        }).onEmpty(() -> {
            FunnyGuilds.getPluginLogger().debug("We're trying to update player scoreboard, but cached scoreboard is null.");

            Scoreboard scoreboard;
            if (this.pluginConfiguration.scoreboard.useSharedScoreboard) {
                scoreboard = player.getScoreboard();
            }
            else {
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                player.setScoreboard(scoreboard);
            }
            cache.setScoreboard(scoreboard);
        });
    }
}
