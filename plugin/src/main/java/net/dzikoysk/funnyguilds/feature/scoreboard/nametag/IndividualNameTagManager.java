package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class IndividualNameTagManager {

    private final PluginConfiguration pluginConfiguration;
    private final UserManager userManager;
    private final ScoreboardService scoreboardService;

    public IndividualNameTagManager(FunnyGuilds plugin, ScoreboardService scoreboardService) {
        this.pluginConfiguration = plugin.getPluginConfiguration();
        this.userManager = plugin.getUserManager();
        this.scoreboardService = scoreboardService;
    }

    private Option<IndividualNameTag> getOrCreateNameTag(User user) {
        // Ensure user has their own scoreboard
        this.scoreboardService.updatePlayer(user);

        UserCache userCache = user.getCache();
        userCache.getIndividualNameTag().onEmpty(() -> {
            IndividualNameTag nameTag = new IndividualNameTag(this.pluginConfiguration, user);
            nameTag.initialize();
            userCache.setIndividualNameTag(nameTag);
        });
        return userCache.getIndividualNameTag();
    }

    // Update everyone to everyone
    public void updatePlayers() {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(player -> this.userManager.findByUuid(player.getUniqueId()))
                .forEach(this::updatePlayer);
    }

    // Update specific observer to everyone (targets) and everyone to specific observer
    public void updatePlayer(User observer) {
        Option<IndividualNameTag> observerNameTag = observer.isOnline() ? this.getOrCreateNameTag(observer) : Option.none();
        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(target -> this.userManager.findByUuid(target.getUniqueId()))
                .forEach(target -> {
                    this.getOrCreateNameTag(target).peek(nameTag -> nameTag.updatePlayer(observer));
                    // Also update target to observer (so relational placeholders could be as much real-time as possible)
                    observerNameTag.peek(nameTag -> nameTag.updatePlayer(target));
                });
    }

}
