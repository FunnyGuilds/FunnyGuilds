package net.dzikoysk.funnyguilds.feature.prefix;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import panda.std.Option;

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
        Scoreboard cachedScoreboard = cache.getScoreboard();

        if (cachedScoreboard == null) {
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

            return;
        }

        try {
            player.setScoreboard(cachedScoreboard);
        } catch (IllegalStateException e) {
            FunnyGuilds.getPluginLogger().warning("[IndividualPrefix] java.lang.IllegalStateException: Cannot set scoreboard for invalid CraftPlayer (" + player.getClass() + ")");
        }
    }

    public static void addGuild(Guild to) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = UserUtils.get(player.getUniqueId());
            IndividualPrefix prefix = user.getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.addGuild(to);
        }
        
        updatePlayers();
    }

    public static void addPlayer(String player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            User user = UserUtils.get(p.getUniqueId());
            IndividualPrefix prefix = user.getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.addPlayer(player);
        }
        
        updatePlayers();
    }

    public static void removeGuild(Guild guild) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = UserUtils.get(player.getUniqueId());
            IndividualPrefix prefix = user.getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.removeGuild(guild);
        }
        
        updatePlayers();
    }

    public static void removePlayer(String player) {
        for (Player ps : Bukkit.getOnlinePlayers()) {
            User user = UserUtils.get(ps.getUniqueId());
            IndividualPrefix prefix = user.getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.removePlayer(player);
        }
        
        updatePlayers();
    }

}
