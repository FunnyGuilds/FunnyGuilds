package net.dzikoysk.funnyguilds.element;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import net.dzikoysk.funnyguilds.basic.user.UserManager;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class IndividualPrefixManager {

    private final FunnyGuilds plugin;
    private final UserManager userManager;

    public IndividualPrefixManager(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    public void updatePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayer(player);
        }
    }

    public void updatePlayer(Player player) {
        if (!player.isOnline()) {
            return;
        }

        User user = userManager.getUser(player);
        UserCache cache = user.getCache();
        Scoreboard cachedScoreboard = cache.getScoreboard();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (cachedScoreboard == null) {
            FunnyGuilds.getPluginLogger().debug(
                    "We're trying to update player scoreboard, but cached scoreboard is null (server has been reloaded?)");

            Bukkit.getScheduler().runTask(FunnyGuilds.getInstance(), () -> {
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
                    IndividualPrefix prefix = new IndividualPrefix(user, plugin);
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

    public void addGuild(Guild to) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            IndividualPrefix prefix = userManager.getUser(player).getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.addGuild(to);
        }
        
        updatePlayers();
    }

    public void addPlayer(String player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            IndividualPrefix prefix = userManager.getUser(p).getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.addPlayer(player);
        }
        
        updatePlayers();
    }

    public void removeGuild(Guild guild) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            IndividualPrefix prefix = userManager.getUser(player).getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.removeGuild(guild);
        }
        
        updatePlayers();
    }

    public void removePlayer(String player) {
        for (Player ps : Bukkit.getOnlinePlayers()) {
            IndividualPrefix prefix = userManager.getUser(ps).getCache().getIndividualPrefix();

            if (prefix == null) {
                continue;
            }

            prefix.removePlayer(player);
        }
        
        updatePlayers();
    }

}
