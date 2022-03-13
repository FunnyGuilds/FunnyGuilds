package net.dzikoysk.funnyguilds.feature.prefix;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import panda.std.stream.PandaStream;

public class DummyManager {

    public static void updatePlayers() {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(onlinePlayer -> FunnyGuilds.getInstance().getUserManager().findByUuid(onlinePlayer.getUniqueId()))
                .forEach(DummyManager::updateScore);
    }

    public static void updateScore(User user) {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(onlinePlayer -> FunnyGuilds.getInstance().getUserManager().findByUuid(onlinePlayer.getUniqueId())
                        .map(User::getCache)
                        .map(UserCache::getDummy))
                .forEach(dummy -> dummy.updateScore(user));
    }
}
