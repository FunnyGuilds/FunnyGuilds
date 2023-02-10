package net.dzikoysk.funnyguilds.feature.scoreboard.dummy;

import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.Pair;
import panda.std.stream.PandaStream;

public class DummyManager {

    private final UserManager userManager;
    private final ScoreboardService scoreboardService;

    public DummyManager(UserManager userManager, ScoreboardService scoreboardService) {
        this.userManager = userManager;
        this.scoreboardService = scoreboardService;
    }

    public void updatePlayers() {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .mapOpt(player -> this.userManager.findByUuid(player.getUniqueId())
                        .map(user -> Pair.of(player, user)))
                .forEach(pair -> this.updateScore(pair.getFirst(), pair.getSecond()));
    }

    public void updateScore(Player player, User user) {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(onlinePlayer -> this.userManager.findByUuid(onlinePlayer.getUniqueId()))
                .forEach(onlineUser -> {
                    this.scoreboardService.updatePlayer(player, onlineUser);
                    onlineUser.getCache().getDummy().updateScore(user);
                });
    }

}
