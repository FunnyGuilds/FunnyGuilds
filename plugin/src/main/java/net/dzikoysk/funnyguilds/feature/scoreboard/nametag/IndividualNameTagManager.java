package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import panda.std.stream.PandaStream;

public class IndividualNameTagManager {

    private final FunnyGuilds plugin;

    private final PluginConfiguration pluginConfiguration;
    private final UserManager userManager;
    private final ScoreboardService scoreboardService;

    public IndividualNameTagManager(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.pluginConfiguration = plugin.getPluginConfiguration();
        this.userManager = plugin.getUserManager();
        this.scoreboardService = plugin.getScoreboardService();

        Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::updatePlayers,
                100,
                this.pluginConfiguration.scoreboard.nameTag.updateRate.getSeconds()
        );
    }

    // Ensures specific user has their own nametag
    public void updateNameTag(User user) {
        if (!this.pluginConfiguration.scoreboard.nameTag.enabled) {
            return;
        }

        this.scoreboardService.updatePlayer(user, () -> {
            UserCache userCache = user.getCache();
            userCache.getIndividualNameTag().onEmpty(() -> {
                IndividualNameTag nameTag = new IndividualNameTag(this.plugin, user);
                nameTag.initialize();
                userCache.setIndividualNameTag(nameTag);
            });
        });
    }

    // Update everyone to everyone
    public void updatePlayers() {
        if (!this.pluginConfiguration.scoreboard.nameTag.enabled) {
            return;
        }

        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(player -> this.userManager.findByUuid(player.getUniqueId()))
                .forEach(this::updatePlayer);
    }

    // Update specific player to everyone
    public void updatePlayer(User user) {
        if (!this.pluginConfiguration.scoreboard.nameTag.enabled) {
            return;
        }

        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(player -> this.userManager.findByUuid(player.getUniqueId()))
                .forEach(onlineUser -> {
                    this.updateNameTag(onlineUser);
                    onlineUser.getCache().getIndividualNameTag().peek(nameTag -> nameTag.updatePlayer(user));
                });

        if (user.isOnline()) {
            user.getCache().getIndividualNameTag().peek(IndividualNameTag::updatePlayers);
        }
    }

}
