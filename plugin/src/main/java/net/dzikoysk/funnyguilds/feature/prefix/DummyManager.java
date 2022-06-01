package net.dzikoysk.funnyguilds.feature.prefix;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.stream.PandaStream;

public final class DummyManager {

    private DummyManager() {
    }

    public static void updatePlayers() {
        try (PandaStream<? extends Player> players = PandaStream.of(Bukkit.getOnlinePlayers())) {
            players.map(Player::getUniqueId)
                    .flatMap(FunnyGuilds.getInstance().getUserManager()::findByUuid)
                    .forEach(DummyManager::updateScore);
        }
    }

    public static void updateScore(User user) {
        try (PandaStream<? extends Player> players = PandaStream.of(Bukkit.getOnlinePlayers())) {
            players.map(Player::getUniqueId)
                    .flatMap(FunnyGuilds.getInstance().getUserManager()::findByUuid)
                    .map(User::getCache)
                    .map(UserCache::getDummy)
                    .forEach(dummy -> dummy.updateScore(user));
        }
    }
}
