package net.dzikoysk.funnyguilds.element;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        
        UserCache cache = User.get(player).getCache();

        try {
            player.setScoreboard(cache.getIndividualPrefix().getScoreboard());
        } catch (IllegalStateException e) {
            FunnyLogger.warning("[IndividualPrefix] java.lang.IllegalStateException: Cannot set scoreboard for invalid CraftPlayer (" + player.getClass() + ")");
        }
        
        cache.setScoreboard(cache.getIndividualPrefix().getScoreboard());
    }

    public static void addGuild(Guild to) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            User.get(p).getCache().getIndividualPrefix().addGuild(to);
        }
        
        updatePlayers();
    }

    public static void addPlayer(String player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            User.get(p).getCache().getIndividualPrefix().addPlayer(player);
        }
        
        updatePlayers();
    }

    public static void removeGuild(Guild guild) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User.get(player).getCache().getIndividualPrefix().removeGuild(guild);
        }
        
        updatePlayers();
    }

    public static void removePlayer(String player) {
        for (Player ps : Bukkit.getOnlinePlayers()) {
            User.get(ps).getCache().getIndividualPrefix().removePlayer(player);
        }
        
        updatePlayers();
    }

}
