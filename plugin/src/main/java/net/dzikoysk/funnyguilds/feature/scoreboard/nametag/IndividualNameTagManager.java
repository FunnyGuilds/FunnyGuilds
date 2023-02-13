package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import java.util.List;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Triple;
import panda.std.stream.PandaStream;

public class IndividualNameTagManager {

    private final PluginConfiguration pluginConfiguration;
    private final UserManager userManager;
    private final ScoreboardService scoreboardService;

    public IndividualNameTagManager(PluginConfiguration pluginConfiguration, UserManager userManager, ScoreboardService scoreboardService) {
        this.pluginConfiguration = pluginConfiguration;
        this.userManager = userManager;
        this.scoreboardService = scoreboardService;
    }

    private Option<IndividualNameTag> getOrCreateNameTag(Player player, User user) {
        UserCache userCache = user.getCache();
        return userCache.getIndividualNameTag().orElse(() -> {
            // Ensure user has their own scoreboard
            this.scoreboardService.updatePlayer(player, user);

            IndividualNameTag nameTag = new IndividualNameTag(this.pluginConfiguration, player, user);
            nameTag.initialize();
            userCache.setIndividualNameTag(nameTag);
            return Option.of(nameTag);
        });
    }

    // Update everyone to everyone
    public void updatePlayers() {
        List<NameTagUpdateData> toUpdate = this.getOnlinePlayersToUpdate();
        for (int observerIndex = 0; observerIndex < toUpdate.size(); observerIndex++) {
            NameTagUpdateData observerData = toUpdate.get(observerIndex);
            for (int targetIndex = observerIndex; targetIndex < toUpdate.size(); targetIndex++) {
                NameTagUpdateData targetData = toUpdate.get(targetIndex);

                targetData.getNameTag().peek(nameTag -> nameTag.updatePlayer(observerData.getPlayer(), observerData.getUser()));
                observerData.getNameTag().peek(nameTag -> nameTag.updatePlayer(targetData.getPlayer(), targetData.getUser()));
            }
        }
    }

    // Update specific observer to everyone (targets) and everyone to specific observer
    public void updatePlayer(Player observerPlayer, User observerUser) {
        Option<IndividualNameTag> observerNameTag = observerPlayer != null && observerUser.isOnline()
                ? this.getOrCreateNameTag(observerPlayer, observerUser)
                : Option.none();

        PandaStream.of(this.getOnlinePlayersToUpdate()).forEach(targetData -> {
            targetData.getNameTag().peek(nameTag -> nameTag.updatePlayer(observerPlayer, observerUser));
            // Also update target to observer (so relational placeholders could be as much real-time as possible)
            observerNameTag.peek(nameTag -> nameTag.updatePlayer(targetData.getPlayer(), targetData.getUser()));
        });
    }

    private List<NameTagUpdateData> getOnlinePlayersToUpdate() {
        return PandaStream.of(Bukkit.getOnlinePlayers())
                .mapOpt(player -> this.userManager.findByUuid(player.getUniqueId())
                        .map(user -> new NameTagUpdateData(player, user, this.getOrCreateNameTag(player, user))))
                .toList();
    }

    private static class NameTagUpdateData extends Triple<Player, User, Option<IndividualNameTag>> {

        private final Player player;
        private final User user;
        private final Option<IndividualNameTag> nameTag;

        public NameTagUpdateData(Player player, User user, Option<IndividualNameTag> nameTag) {
            super(player, user, nameTag);
            this.player = player;
            this.user = user;
            this.nameTag = nameTag;
        }

        public Player getPlayer() {
            return this.player;
        }

        public User getUser() {
            return this.user;
        }

        public Option<IndividualNameTag> getNameTag() {
            return this.nameTag;
        }

    }

}
