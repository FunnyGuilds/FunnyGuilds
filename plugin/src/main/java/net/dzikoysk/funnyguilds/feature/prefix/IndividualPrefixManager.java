package net.dzikoysk.funnyguilds.feature.prefix;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class IndividualPrefixManager {

    public static void updatePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayer(player);
        }
    }

    public static void updatePlayer(Player player) {
        if (!player.isOnline()) {
            return;
        }

        FunnyGuilds plugin = FunnyGuilds.getInstance();
        UserManager userManager = plugin.getUserManager();
        PluginConfiguration config = plugin.getPluginConfiguration();

        Option<User> userOption = userManager.findByPlayer(player);
        if (userOption.isEmpty()) {
            return;
        }

        User user = userOption.get();
        UserCache cache = user.getCache();

        cache.getScoreboard().peek(scoreboard -> {
            try {
                player.setScoreboard(scoreboard);
            }
            catch (IllegalStateException e) {
                FunnyGuilds.getPluginLogger().warning("[IndividualPrefix] java.lang.IllegalStateException: Cannot set scoreboard for invalid CraftPlayer (" + player.getClass() + ")");
            }
        }).onEmpty(() -> {
            FunnyGuilds.getPluginLogger().debug(
                    "We're trying to update player scoreboard, but cached scoreboard is null (server has been reloaded?)");

            Bukkit.getScheduler().runTask(plugin, () -> {
                Scoreboard scoreboard;
                if (config.useSharedScoreboard) {
                    scoreboard = player.getScoreboard();
                }
                else {
                    scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                    player.setScoreboard(scoreboard);
                }

                cache.setScoreboard(scoreboard);

                if (config.guildTagEnabled) {
                    IndividualPrefix prefix = new IndividualPrefix(user);
                    prefix.initialize();

                    cache.setIndividualPrefix(prefix);
                }

                cache.getDummy().updateScore(user);
            });
        });
    }

    public static void addGuild(UserManager userManager, Guild guild) {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .map(Player::getUniqueId)
                .flatMap(userManager::findByUuid)
                .map(User::getCache)
                .map(UserCache::getIndividualPrefix)
                .forEach(prefix -> prefix.addGuild(guild));

        updatePlayers();
    }

    public static void addPlayer(UserManager userManager, String player) {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .map(Player::getUniqueId)
                .flatMap(userManager::findByUuid)
                .map(User::getCache)
                .map(UserCache::getIndividualPrefix)
                .forEach(prefix -> prefix.addPlayer(player));

        updatePlayers();
    }

    public static void removeGuild(UserManager userManager, Guild guild) {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .map(Player::getUniqueId)
                .flatMap(userManager::findByUuid)
                .map(User::getCache)
                .map(UserCache::getIndividualPrefix)
                .forEach(prefix -> prefix.removeGuild(guild));

        updatePlayers();
    }

    public static void removePlayer(UserManager userManager, String player) {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .map(Player::getUniqueId)
                .flatMap(userManager::findByUuid)
                .map(User::getCache)
                .map(UserCache::getIndividualPrefix)
                .forEach(prefix -> prefix.removePlayer(player));

        updatePlayers();
    }

}
