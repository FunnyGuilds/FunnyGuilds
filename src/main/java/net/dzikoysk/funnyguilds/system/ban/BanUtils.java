package net.dzikoysk.funnyguilds.system.ban;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BanUtils {

    public static void ban(Guild guild, long time, String reason) {
        guild.setBan(time + System.currentTimeMillis());
        for (User user : guild.getMembers()) {
            ban(user, time, reason);
            Player p = user.getPlayer();
            if (p != null && p.isOnline()) {
                p.kickPlayer(ChatColor.GRAY + "Otrzymano bana za: " + ChatColor.RED + user.getReason());
            }
        }
    }

    public static void ban(User user, long time, String reason) {
        time += System.currentTimeMillis();
        user.setBan(time);
        user.setReason(reason);
    }

    public static void unban(Guild guild) {
        for (User user : guild.getMembers()) {
            unban(user);
        }
    }

    public static void unban(User user) {
        user.setBan(0);
        user.setReason(null);
    }

    public static boolean check(User user) {
        if (System.currentTimeMillis() < user.getBan())
            return true;
        user.setBan(0);
        user.setReason(null);
        return false;
    }

    public static String getBanMessage(User user) {
        DateFormat date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date ban = new Date(user.getBan());
        String message = Messages.getInstance().getMessage("banMessage");
        message = StringUtils.replace(message, "{NEWLINE}", ChatColor.RESET + "\n");
        message = StringUtils.replace(message, "{DATE}", date.format(ban));
        message = StringUtils.replace(message, "{REASON}", user.getReason());
        message = StringUtils.replace(message, "{PLAYER}", user.getName());
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
