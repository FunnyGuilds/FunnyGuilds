package net.dzikoysk.funnyguilds.feature.scoreboard;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import panda.std.stream.PandaStream;

public class ScoreboardService {

    private final FunnyGuilds plugin;

    private final PluginConfiguration pluginConfiguration;
    private final UserManager userManager;

    public ScoreboardService(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.pluginConfiguration = plugin.getPluginConfiguration();
        this.userManager = plugin.getUserManager();
    }

    public void updatePlayers(Runnable afterUpdate) {
        if (!this.pluginConfiguration.scoreboard.enabled) {
            return;
        }

        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(player -> this.userManager.findByUuid(player.getUniqueId()))
                .forEach(user -> this.updatePlayer(user, afterUpdate));
    }

    public void updatePlayer(User user, Runnable afterUpdate) {
        if (!this.pluginConfiguration.scoreboard.enabled) {
            return;
        }

        Player player = Bukkit.getPlayer(user.getUUID());
        if (player == null) {
            return;
        }

        UserCache cache = user.getCache();
        cache.getScoreboard().peek(scoreboard -> {
            try {
                player.setScoreboard(scoreboard);
                afterUpdate.run();
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
            afterUpdate.run();
        });
    }
}
