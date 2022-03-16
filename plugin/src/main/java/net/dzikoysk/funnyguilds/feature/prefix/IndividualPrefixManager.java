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

    private final FunnyGuilds plugin;

    private final PluginConfiguration pluginConfiguration;
    private final UserManager userManager;

    public IndividualPrefixManager(FunnyGuilds plugin) {
        this.plugin = plugin;

        this.pluginConfiguration = plugin.getPluginConfiguration();
        this.userManager = plugin.getUserManager();
    }

    public void updatePlayers() {
        Bukkit.getOnlinePlayers().forEach(this::updatePlayer);
    }

    public void updatePlayer(Player player) {
        if (!player.isOnline()) {
            return;
        }

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
                if (pluginConfiguration.useSharedScoreboard) {
                    scoreboard = player.getScoreboard();
                }
                else {
                    scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                    player.setScoreboard(scoreboard);
                }

                cache.setScoreboard(scoreboard);

                if (pluginConfiguration.guildTagEnabled) {
                    IndividualPrefix prefix = new IndividualPrefix(user);
                    prefix.initialize();

                    cache.setIndividualPrefix(prefix);
                }

                cache.getDummy().updateScore(user);
            });
        });
    }

    public void addGuild(Guild guild) {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .map(Player::getUniqueId)
                .flatMap(userManager::findByUuid)
                .map(User::getCache)
                .map(UserCache::getIndividualPrefix)
                .forEach(prefix -> prefix.addGuild(guild));

        updatePlayers();
    }

    public void addPlayer(String player) {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .map(Player::getUniqueId)
                .flatMap(userManager::findByUuid)
                .map(User::getCache)
                .map(UserCache::getIndividualPrefix)
                .forEach(prefix -> prefix.addPlayer(player));

        updatePlayers();
    }

    public void removeGuild(Guild guild) {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .map(Player::getUniqueId)
                .flatMap(userManager::findByUuid)
                .map(User::getCache)
                .map(UserCache::getIndividualPrefix)
                .forEach(prefix -> prefix.removeGuild(guild));

        updatePlayers();
    }

    public void removePlayer(String player) {
        PandaStream.of(Bukkit.getOnlinePlayers())
                .map(Player::getUniqueId)
                .flatMap(userManager::findByUuid)
                .map(User::getCache)
                .map(UserCache::getIndividualPrefix)
                .forEach(prefix -> prefix.removePlayer(player));

        updatePlayers();
    }

}
