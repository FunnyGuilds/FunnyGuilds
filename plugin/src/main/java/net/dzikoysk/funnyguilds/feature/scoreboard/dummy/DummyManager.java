package net.dzikoysk.funnyguilds.feature.scoreboard.dummy;

import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.scoreboard.AbstractScoreboardHandler;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardService;
import net.dzikoysk.funnyguilds.rank.RankService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.entity.Player;

public class DummyManager extends AbstractScoreboardHandler<Dummy> {

    private final RankService rankService;

    public DummyManager(PluginConfiguration pluginConfiguration, RankService rankService, UserManager userManager, ScoreboardService scoreboardService) {
        super(pluginConfiguration, userManager, scoreboardService);
        this.rankService = rankService;
    }

    @Override
    protected Dummy getOrCreateData(Player player, User user) {
        UserCache userCache = user.getCache();
        return userCache.getDummy().orElseGet(() -> {
            // Ensure user has their own scoreboard
            this.scoreboardService.updatePlayer(player, user);

            Dummy dummy = new Dummy(this.pluginConfiguration, this.rankService, user);
            dummy.initialize();
            userCache.setDummy(dummy);
            return dummy;
        });
    }

    @Override
    protected void update(AbstractScoreboardHandler<Dummy>.UpdateData observerData, AbstractScoreboardHandler<Dummy>.UpdateData targetData) {
        observerData.getData().ifPresent(dummy -> dummy.updatePlayer(targetData.getUser()));
        targetData.getData().ifPresent(dummy -> dummy.updatePlayer(observerData.getUser()));
    }


}
