package net.dzikoysk.funnyguilds.system.war;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WarSystem {

    private static WarSystem instance;

    public WarSystem() {
        instance = this;
    }

    public static WarSystem getInstance() {
        if (instance == null)
            new WarSystem();
        return instance;
    }

    public void attack(Player player, Guild guild) {
        User user = User.get(player);
        if (!user.hasGuild()) {
            WarUtils.message(player, 0);
            return;
        }
        Guild attacker = user.getGuild();
        if (attacker.equals(guild))
            return;
        if (attacker.getAllies().contains(guild)) {
            WarUtils.message(player, 1);
            return;
        }
        if (guild.getAttacked() != 0 && guild.getAttacked() + Settings.getInstance().warWait > System.currentTimeMillis()) {
            WarUtils.message(player, 2, (guild.getAttacked() + Settings.getInstance().warWait) - System.currentTimeMillis());
            return;
        }
        guild.setAttacked(System.currentTimeMillis());
        guild.removeLive();
        if (guild.getLives() < 1)
            conquer(attacker, guild);
        else {
            for (User u : attacker.getMembers()) {
                Player p = Bukkit.getPlayer(u.getName());
                if (p != null)
                    WarUtils.message(p, 3, guild);
            }
            for (User u : guild.getMembers()) {
                Player p = Bukkit.getPlayer(u.getName());
                if (p != null)
                    WarUtils.message(p, 4, attacker);
            }
        }
    }

    public void conquer(Guild conqueror, Guild loser) {
        String message = WarUtils.getWinMessage(conqueror, loser);
        for (User user : conqueror.getMembers()) {
            Player player = Bukkit.getPlayer(user.getName());
            if (player != null)
                player.sendMessage(message);
        }
        message = WarUtils.getLoseMessage(conqueror, loser);
        for (User user : loser.getMembers()) {
            Player player = Bukkit.getPlayer(user.getName());
            if (player != null)
                player.sendMessage(message);
        }

        GuildUtils.deleteGuild(loser);
        conqueror.addLive();

        message = WarUtils.getBroadcastMessage(conqueror, loser);
        Bukkit.broadcastMessage(message);
    }
}
