package net.dzikoysk.funnyguilds.element;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

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

        User user = User.get(player);
        UserCache cache = user.getCache();
        Scoreboard cachedScoreboard = cache.getScoreboard();

        if (cachedScoreboard == null) {
            FunnyGuilds.getInstance().getPluginLogger().debug(
                    "We're trying to update player scoreboard, but cached scoreboard is null (server has been reloaded?)");

            Bukkit.getScheduler().runTask(FunnyGuilds.getInstance(), () -> {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                player.setScoreboard(scoreboard);
                cache.setScoreboard(scoreboard);

                IndividualPrefix prefix = new IndividualPrefix(user);
                prefix.initialize();

                cache.setIndividualPrefix(prefix);
                cache.getDummy().updateScore(user);
            });

            return;
        }

        try {
            player.setScoreboard(cachedScoreboard);
        } catch (IllegalStateException e) {
            FunnyGuilds.getInstance().getPluginLogger().warning("[IndividualPrefix] java.lang.IllegalStateException: Cannot set scoreboard for invalid CraftPlayer (" + player.getClass() + ")");
        }
    }

    public static void addGuild(Guild to) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            IndividualPrefix prefix = User.get(player).getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.addGuild(to);
        }
        
        updatePlayers();
    }

    public static void addPlayer(String player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            IndividualPrefix prefix = User.get(p).getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.addPlayer(player);
        }
        
        updatePlayers();
    }

    public static void removeGuild(Guild guild) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            IndividualPrefix prefix = User.get(player).getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.removeGuild(guild);
        }
        
        updatePlayers();
    }

    public static void removePlayer(String player) {
        for (Player ps : Bukkit.getOnlinePlayers()) {
            IndividualPrefix prefix = User.get(ps).getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.removePlayer(player);
        }
        
        updatePlayers();
    }

}
