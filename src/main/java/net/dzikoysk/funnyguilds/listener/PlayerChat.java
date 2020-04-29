package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        User user = User.get(player);
        PluginConfiguration c = FunnyGuilds.getInstance().getPluginConfiguration();
        
        if (user.hasGuild()) {
            Guild guild = user.getGuild();
            String message = event.getMessage();
            if (sendGuildMessage(event, message, c, player, guild)) {
                return;
            }
        }
        
        int points = user.getRank().getPoints();
        String format = event.getFormat();
        
        format = StringUtils.replace(format, "{RANK}", StringUtils.replace(c.chatRank, "{RANK}", String.valueOf(user.getRank().getPosition())));
        format = StringUtils.replace(format, "{POINTS}", c.chatPoints);
        format = StringUtils.replace(format, "{POINTS-FORMAT}", IntegerRange.inRange(points, c.pointsFormat, "POINTS"));
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

    private boolean sendGuildMessage(AsyncPlayerChatEvent event, String message, PluginConfiguration c, Player player, Guild guild) {
        if (sendMessageToAllGuilds(event, message, c, player, guild)) {
            return true;
        }
        
        if (sendMessageToGuildAllies(event, message, c, player, guild)) {
            return true;
        }
        
        return sendMessageToGuildMembers(event, message, c, player, guild);
    }

    private void spy(Player player, String message) {
        String spyMessage = ChatColor.GOLD + "[Spy] " + ChatColor.GRAY + player.getName() + ": " + ChatColor.WHITE + message;

        for (Player looped : Bukkit.getOnlinePlayers()) {
            if (User.get(looped).getCache().isSpy()) {
                looped.sendMessage(spyMessage);
            }
        }
    }

    private boolean sendMessageToGuildMembers(AsyncPlayerChatEvent event, String message, PluginConfiguration c, Player player, Guild guild) {
        String guildPrefix = c.chatPriv;
        int prefixLength = guildPrefix.length();
        
        if (message.length() > prefixLength && message.substring(0, prefixLength).equals(guildPrefix)) {
            String resultMessage = c.chatPrivDesign;
            
            resultMessage = StringUtils.replace(resultMessage, "{PLAYER}", player.getName());
            resultMessage = StringUtils.replace(resultMessage, "{TAG}", guild.getTag());
            resultMessage = StringUtils.replace(resultMessage, "{POS}",
                    StringUtils.replace(c.chatPosition, "{POS}", getPositionString(User.get(player), c)));
            
            String messageWithoutPrefix = event.getMessage().substring(prefixLength).trim();
            resultMessage = StringUtils.replace(resultMessage, "{MESSAGE}", messageWithoutPrefix);

            this.spy(player, messageWithoutPrefix);
            this.sendMessageToGuild(guild, player, resultMessage);
            
            event.setCancelled(true);
            return true;
        }
        
        return false;
    }

    private boolean sendMessageToGuildAllies(AsyncPlayerChatEvent event, String message, PluginConfiguration c, Player player, Guild guild) {
        String allyPrefix = c.chatAlly;
        int prefixLength = allyPrefix.length();
        
        if (message.length() > prefixLength && message.substring(0, prefixLength).equals(allyPrefix)) {
            String resultMessage = c.chatAllyDesign;
            
            resultMessage = StringUtils.replace(resultMessage, "{PLAYER}", player.getName());
            resultMessage = StringUtils.replace(resultMessage, "{TAG}", guild.getTag());
            resultMessage = StringUtils.replace(resultMessage, "{POS}",
                    StringUtils.replace(c.chatPosition, "{POS}", getPositionString(User.get(player), c)));

            String subMessage = event.getMessage().substring(prefixLength).trim();
            resultMessage = StringUtils.replace(resultMessage, "{MESSAGE}", subMessage);

            this.spy(player, subMessage);
            this.sendMessageToGuild(guild, player, resultMessage);

            for (Guild g : guild.getAllies()) {
                this.sendMessageToGuild(g, player, resultMessage);
            }
            
            event.setCancelled(true);
            return true;
        }
        
        return false;
    }

    private boolean sendMessageToAllGuilds(AsyncPlayerChatEvent event, String message, PluginConfiguration c, Player player, Guild guild) {
        String allGuildsPrefix = c.chatGlobal;
        int prefixLength = allGuildsPrefix.length();
        
        if (message.length() > prefixLength && message.substring(0, prefixLength).equals(allGuildsPrefix)) {
            String resultMessage = c.chatGlobalDesign;
            
            resultMessage = StringUtils.replace(resultMessage, "{PLAYER}", player.getName());
            resultMessage = StringUtils.replace(resultMessage, "{TAG}", guild.getTag());
            resultMessage = StringUtils.replace(resultMessage, "{POS}",
                    StringUtils.replace(c.chatPosition, "{POS}", getPositionString(User.get(player), c)));

            String subMessage = event.getMessage().substring(prefixLength).trim();
            resultMessage = StringUtils.replace(resultMessage, "{MESSAGE}", subMessage);

            this.spy(player, subMessage);

            for (Guild g : GuildUtils.getGuilds()) {
                this.sendMessageToGuild(g, player, resultMessage);
            }
            
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
                continue;
            }
            
            if (!p.equals(player) || !user.getCache().isSpy()) {
                p.sendMessage(message);
            }
        }
    }

    private String getPositionString(User u, PluginConfiguration c) {
        if (u.isOwner()) {
            return c.chatPositionLeader;
        }
        
        if (u.isDeputy()) {
            return c.chatPositionDeputy;
        }
        
        return c.chatPositionMember;
    }

}
