package net.dzikoysk.funnyguilds.element;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.FunnyLogger;
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
        
        User user = User.get(player);
        try {
            player.setScoreboard(user.getIndividualPrefix().getScoreboard());
        } catch (IllegalStateException e) {
            FunnyLogger.warning("[IndividualPrefix] java.lang.IllegalStateException: Cannot set scoreboard for invalid CraftPlayer (" + player.getClass() + ")");
        }
        
        user.setScoreboard(user.getIndividualPrefix().getScoreboard());
    }

    public static void addGuild(Guild to) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            User.get(p).getIndividualPrefix().addGuild(to);
        }
        
        updatePlayers();
    }

    public static void addPlayer(String player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            User.get(p).getIndividualPrefix().addPlayer(player);
        }
        
        updatePlayers();
    }

    public static void removeGuild(Guild guild) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User.get(player).getIndividualPrefix().removeGuild(guild);
        }
        
        updatePlayers();
    }

    public static void removePlayer(String player) {
        for (Player ps : Bukkit.getOnlinePlayers()) {
            User.get(ps).getIndividualPrefix().removePlayer(player);
        }
        
        updatePlayers();
    }
}
