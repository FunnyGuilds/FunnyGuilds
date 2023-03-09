package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.scoreboard.AbstractScoreboardHandler;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.entity.Player;

public class IndividualNameTagManager extends AbstractScoreboardHandler<IndividualNameTag> {

    public IndividualNameTagManager(PluginConfiguration pluginConfiguration, UserManager userManager, ScoreboardService scoreboardService) {
        super(pluginConfiguration, userManager, scoreboardService);
    }

    @Override
    protected IndividualNameTag getOrCreateData(Player player, User user) {
        UserCache userCache = user.getCache();
        return userCache.getIndividualNameTag().orElseGet(() -> {
            // Ensure user has their own scoreboard
            this.scoreboardService.updatePlayer(player, user);

            IndividualNameTag nameTag = new IndividualNameTag(this.pluginConfiguration, player, user);
            nameTag.initialize();
            userCache.setIndividualNameTag(nameTag);
            return nameTag;
        });
    }

    @Override
    protected void update(AbstractScoreboardHandler<IndividualNameTag>.UpdateData observerData, AbstractScoreboardHandler<IndividualNameTag>.UpdateData targetData) {
        observerData.getData().peek(nameTag -> nameTag.updatePlayer(targetData.getPlayer(), targetData.getUser()));
        targetData.getData().peek(nameTag -> nameTag.updatePlayer(observerData.getPlayer(), observerData.getUser()));
    }

}
