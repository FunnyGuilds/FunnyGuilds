package net.dzikoysk.funnyguilds.feature.scoreboard.dummy;

import java.util.List;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Pair;
import panda.std.stream.PandaStream;

public class DummyManager {

    private final PluginConfiguration pluginConfiguration;
    private final UserManager userManager;
    private final ScoreboardService scoreboardService;

    public DummyManager(PluginConfiguration pluginConfiguration, UserManager userManager, ScoreboardService scoreboardService) {
        this.pluginConfiguration = pluginConfiguration;
        this.userManager = userManager;
        this.scoreboardService = scoreboardService;
    }

    private Option<Dummy> getOrCreateDummy(Player player, User user) {
        UserCache userCache = user.getCache();
        return userCache.getDummy().orElse(() -> {
            // Ensure user has their own scoreboard
            this.scoreboardService.updatePlayer(player, user);

            Dummy dummy = new Dummy(this.pluginConfiguration, user);
            dummy.initialize();
            userCache.setDummy(dummy);
            return Option.of(dummy);
        });
    }

    // Update everyone to everyone
    public void updatePlayers() {
        List<DummyUpdateData> toUpdate = this.getOnlinePlayersToUpdate();
        for (int observerIndex = 0; observerIndex < toUpdate.size(); observerIndex++) {
            DummyUpdateData observerData = toUpdate.get(observerIndex);
            for (int targetIndex = observerIndex; targetIndex < toUpdate.size(); targetIndex++) {
                DummyUpdateData targetData = toUpdate.get(targetIndex);

                targetData.getDummy().peek(nameTag -> nameTag.updatePlayer(observerData.getUser()));
                observerData.getDummy().peek(nameTag -> nameTag.updatePlayer(targetData.getUser()));
            }
        }
    }

    // Update specific observer to everyone (targets) and everyone to specific observer
    public void updatePlayer(Player observerPlayer, User observerUser) {
        Option<Dummy> observerDummy = observerPlayer != null && observerUser.isOnline()
                ? this.getOrCreateDummy(observerPlayer, observerUser)
                : Option.none();

        PandaStream.of(this.getOnlinePlayersToUpdate()).forEach(targetData -> {
            targetData.getDummy().peek(dummy -> dummy.updatePlayer(observerUser));
            // Also update target to observer (just in case)
            observerDummy.peek(dummy -> dummy.updatePlayer(targetData.getUser()));
        });
    }

    private List<DummyUpdateData> getOnlinePlayersToUpdate() {
        return PandaStream.of(Bukkit.getOnlinePlayers())
                .mapOpt(player -> this.userManager.findByUuid(player.getUniqueId())
                        .map(user -> new DummyUpdateData(user, this.getOrCreateDummy(player, user))))
                .toList();
    }

    private static class DummyUpdateData extends Pair<User, Option<Dummy>> {

        private final User user;
        private final Option<Dummy> dummy;

        public DummyUpdateData(User user, Option<Dummy> dummy) {
            super(user, dummy);
            this.user = user;
            this.dummy = dummy;
        }

        public User getUser() {
            return this.user;
        }

        public Option<Dummy> getDummy() {
            return this.dummy;
        }

    }

}
