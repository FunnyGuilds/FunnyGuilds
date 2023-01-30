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

    private final PluginConfiguration pluginConfiguration;
    private final UserManager userManager;
    private final ScoreboardService scoreboardService;

    public IndividualNameTagManager(FunnyGuilds plugin) {
        this.pluginConfiguration = plugin.getPluginConfiguration();
        this.userManager = plugin.getUserManager();
        this.scoreboardService = plugin.getScoreboardService();

        if (!this.isNameTagEnabled()) {
            return;
        }
        Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::updatePlayers,
                100,
                this.pluginConfiguration.scoreboard.nametag.updateRate.getSeconds() * 20L
        );
    }

    // Ensures specific user has their own nametag
    private void updateNameTag(User user) {
        if (!this.isNameTagEnabled()) {
            return;
        }

        // Ensure user has their own scoreboard
        this.scoreboardService.updatePlayer(user);

        UserCache userCache = user.getCache();
        userCache.getIndividualNameTag().onEmpty(() -> {
            IndividualNameTag nameTag = new IndividualNameTag(this.pluginConfiguration, user);
            nameTag.initialize();
            userCache.setIndividualNameTag(nameTag);
        });
    }

    // Update everyone to everyone
    public void updatePlayers() {
        if (!this.isNameTagEnabled()) {
            return;
        }

        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(player -> this.userManager.findByUuid(player.getUniqueId()))
                .forEach(this::updatePlayer);
    }

    // Update specific observer to everyone (targets) and everyone to specific observer
    public void updatePlayer(User observer) {
        if (!this.isNameTagEnabled()) {
            return;
        }

        boolean isOnline = observer.isOnline();
        // Ensure observer has their own nametag
        if (isOnline) {
            this.updateNameTag(observer);
        }

        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(target -> this.userManager.findByUuid(target.getUniqueId()))
                .forEach(target -> {
                    this.updateNameTag(target);
                    target.getCache().getIndividualNameTag().peek(nameTag -> nameTag.updatePlayer(observer));

                    // Also update target to observer
                    if (isOnline) {
                        observer.getCache().getIndividualNameTag().peek(nameTag -> nameTag.updatePlayer(target));
                    }
                });
    }

    private boolean isNameTagEnabled() {
        return this.pluginConfiguration.scoreboard.nametag.enabled;
    }

}
