package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        User user = User.get(player);
        Settings c = Settings.getInstance();
        if (user.hasGuild()) {
            Guild guild = user.getGuild();
            String message = event.getMessage();
            if (chat(event, message, c, player, guild))
                return;
        }
        String format = event.getFormat();
        format = StringUtils.replace(format, "{RANK}", StringUtils
                .replace(c.chatRank, "{RANK}", Integer.toString(RankManager.getInstance().getPosition(user))));
        format = StringUtils.replace(format, "{POINTS}", StringUtils
                .replace(c.chatPoints, "{POINTS}", Integer.toString(user.getRank().getPoints())));
        if (user.hasGuild())
            format = StringUtils.replace(format, "{TAG}", StringUtils
                    .replace(c.chatGuild, "{TAG}", user.getGuild().getTag()));
        else
            format = StringUtils.replace(format, "{TAG}", "");
        event.setFormat(format);
    }

    private boolean chat(AsyncPlayerChatEvent event, String message, Settings c, Player player, Guild guild) {
        if (global(event, message, c, player, guild))
            return true;
        if (ally(event, message, c, player, guild))
            return true;
        if (priv(event, message, c, player, guild))
            return true;
        return false;
    }

    private boolean priv(AsyncPlayerChatEvent event, String message, Settings c, Player player, Guild guild) {
        String priv = c.chatPriv;
        int length = priv.length();
        if (message.length() > length && message.substring(0, length).equals(priv)) {
            String format = c.chatPrivDesign;
            format = StringUtils.replace(format, "{PLAYER}", player.getName());
            format = StringUtils.replace(format, "{TAG}", guild.getTag());
            format = StringUtils.replace(format, "{MESSAGE}", event.getMessage().substring(length));
            for (User u : guild.getMembers()) {
                Player p = Bukkit.getPlayer(u.getName());
                if (p != null)
                    p.sendMessage(format);
            }
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    private boolean ally(AsyncPlayerChatEvent event, String message, Settings c, Player player, Guild guild) {
        String ally = c.chatAlly;
        int length = ally.length();
        if (message.length() > length && message.substring(0, length).equals(ally)) {
            String format = c.chatAllyDesign;
            format = StringUtils.replace(format, "{PLAYER}", player.getName());
            format = StringUtils.replace(format, "{TAG}", guild.getTag());
            format = StringUtils.replace(format, "{MESSAGE}", event.getMessage().substring(length));
            for (User u : guild.getMembers()) {
                Player p = Bukkit.getPlayer(u.getName());
                if (p != null)
                    p.sendMessage(format);
            }
            for (Guild g : guild.getAllies()) {
                for (User u : g.getMembers()) {
                    Player p = Bukkit.getPlayer(u.getName());
                    if (p != null)
                        p.sendMessage(format);
                }
            }
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    private boolean global(AsyncPlayerChatEvent event, String message, Settings c, Player player, Guild guild) {
        String global = c.chatGlobal;
        int length = global.length();
        if (message.length() > length && message.substring(0, length).equals(global)) {
            String format = c.chatGlobalDesign;
            format = StringUtils.replace(format, "{PLAYER}", player.getName());
            format = StringUtils.replace(format, "{TAG}", guild.getTag());
            format = StringUtils.replace(format, "{MESSAGE}", event.getMessage().substring(length));
            for (Guild g : GuildUtils.getGuilds()) {
                for (User u : g.getMembers()) {
                    Player p = Bukkit.getPlayer(u.getName());
                    if (p != null)
                        p.sendMessage(format);
                }
            }
            event.setCancelled(true);
            return true;
        }
        return false;
    }
}
