package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {

    private final FunnyGuilds plugin;

    public PlayerChat(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        User user = User.get(player);
        PluginConfig c = Settings.getConfig();
        
        if (user.hasGuild()) {
            Guild guild = user.getGuild();
            String message = event.getMessage();
            if (chat(event, message, c, player, guild)) {
                return;
            }
        }
        
        int points = user.getRank().getPoints();
        String format = event.getFormat();
        
        format = StringUtils.replace(format, "{RANK}", StringUtils.replace(c.chatRank, "{RANK}", String.valueOf(user.getRank().getPosition())));
        format = StringUtils.replace(format, "{POINTS}", c.chatPoints);
        format = StringUtils.replace(format, "{POINTS-FORMAT}", IntegerRange.inRange(points, c.pointsFormat));
        format = StringUtils.replace(format, "{POINTS}", String.valueOf(points));
        
        if (user.hasGuild()) {
            format = StringUtils.replace(format, "{TAG}", StringUtils.replace(c.chatGuild, "{TAG}", user.getGuild().getTag()));
            format = StringUtils.replace(format, "{POS}", StringUtils.replace(c.chatPosition, "{POS}", getPositionString(user, c)));
        } else {
            format = StringUtils.replace(format, "{TAG}", "");
            format = StringUtils.replace(format, "{POS}", "");
        }
        
        event.setFormat(format);
    }

    private boolean chat(AsyncPlayerChatEvent event, String message, PluginConfig c, Player player, Guild guild) {
        if (global(event, message, c, player, guild)) {
            return true;
        }
        
        if (ally(event, message, c, player, guild)) {
            return true;
        }
        
        return priv(event, message, c, player, guild);
    }

    private void spy(Player player, String message) {
        String spyMessage = ChatColor.GOLD + "[Spy] " + ChatColor.GRAY + player.getName() + ": " + ChatColor.WHITE + message;
        for (Player looped : plugin.getServer().getOnlinePlayers()) {
            if (User.get(looped).isSpy()) {
                looped.sendMessage(spyMessage);
            }
        }
    }

    private boolean priv(AsyncPlayerChatEvent event, String message, PluginConfig c, Player player, Guild guild) {
        String priv = c.chatPriv;
        int length = priv.length();
        
        if (message.length() > length && message.substring(0, length).equals(priv)) {
            String format = c.chatPrivDesign;
            
            format = StringUtils.replace(format, "{PLAYER}", player.getName());
            format = StringUtils.replace(format, "{TAG}", guild.getTag());
            format = StringUtils.replace(format, "{POS}", StringUtils.replace(c.chatPosition, "{POS}", getPositionString(User.get(player), c)));
            
            String subMessage = event.getMessage().substring(length);
            this.spy(player, subMessage);
            
            format = StringUtils.replace(format, "{MESSAGE}", subMessage);
            this.sendMessageToGuild(guild, player, format);
            this.spy(player, message);
            
            event.setCancelled(true);
            return true;
        }
        
        return false;
    }

    private boolean ally(AsyncPlayerChatEvent event, String message, PluginConfig c, Player player, Guild guild) {
        String ally = c.chatAlly;
        int length = ally.length();
        
        if (message.length() > length && message.substring(0, length).equals(ally)) {
            String format = c.chatAllyDesign;
            
            format = StringUtils.replace(format, "{PLAYER}", player.getName());
            format = StringUtils.replace(format, "{TAG}", guild.getTag());
            format = StringUtils.replace(format, "{POS}", StringUtils.replace(c.chatPosition, "{POS}", getPositionString(User.get(player), c)));
            

            String subMessage = event.getMessage().substring(length);
            this.spy(player, subMessage);
            
            format = StringUtils.replace(format, "{MESSAGE}", subMessage);
            for (User u : guild.getMembers()) {
                Player p = u.getPlayer();
                if (p != null) {
                    p.sendMessage(format);
                }
            }
            
            for (Guild g : guild.getAllies()) {
                this.sendMessageToGuild(g, player, format);
            }
            
            event.setCancelled(true);
            return true;
        }
        
        return false;
    }

    private boolean global(AsyncPlayerChatEvent event, String message, PluginConfig c, Player player, Guild guild) {
        String global = c.chatGlobal;
        int length = global.length();
        
        if (message.length() > length && message.substring(0, length).equals(global)) {
            String format = c.chatGlobalDesign;
            
            format = StringUtils.replace(format, "{PLAYER}", player.getName());
            format = StringUtils.replace(format, "{TAG}", guild.getTag());
            format = StringUtils.replace(format, "{POS}", StringUtils.replace(c.chatPosition, "{POS}", getPositionString(User.get(player), c)));
            
            String subMessage = event.getMessage().substring(length);
            this.spy(player, subMessage);
            
            format = StringUtils.replace(format, "{MESSAGE}", subMessage);
            for (Guild g : GuildUtils.getGuilds()) {
                this.sendMessageToGuild(g, player, format);
            }
            
            this.spy(player, message);
            
            event.setCancelled(true);
            return true;
        }
        
        return false;
    }

    private void sendMessageToGuild(Guild guild, Player player, String message) {
        if (guild == null || player == null || !player.isOnline()) {
            return;
        }

        for (User user : guild.getOnlineMembers()) {
            Player p = user.getPlayer();
            
            if (p == null) {
                return;
            }
            
            if (!p.equals(player) || !user.isSpy()) {
                p.sendMessage(message);
            }
        }
    }
    
    private String getPositionString(User u, PluginConfig c) {
        if (u.isOwner()) {
            return c.chatPositionLeader;
        }
        
        if (u.isDeputy()) {
            return c.chatPositionDeputy;
        }
        
        return c.chatPositionMember;
    }
}
