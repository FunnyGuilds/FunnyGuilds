package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.command.admin.AxcSpy;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

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
        String format = event.getFormat();
        format = StringUtils.replace(format, "{RANK}", StringUtils.replace(c.chatRank, "{RANK}", Integer.toString(RankManager.getInstance().getPosition(user))));
        format = StringUtils.replace(format, "{POINTS}", StringUtils.replace(c.chatPoints, "{POINTS}", Integer.toString(user.getRank().getPoints())));
        if (user.hasGuild()) {
            format = StringUtils.replace(format, "{TAG}", StringUtils.replace(c.chatGuild, "{TAG}", user.getGuild().getTag()));
        } else {
            format = StringUtils.replace(format, "{TAG}", "");
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
            String subMessage = event.getMessage().substring(length);
            this.spy(player, subMessage);
            format = StringUtils.replace(format, "{MESSAGE}", subMessage);
            for (User u : guild.getMembers()) {
                Player p = this.plugin.getServer().getPlayer(u.getName());
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
        for (User user : guild.getMembers()) {
            Player loopedPlayer = this.plugin.getServer().getPlayer(user.getName());
            if (loopedPlayer != null) {
                if (user.getPlayer().equals(player)) {
                    if (!user.isSpy()) {
                        loopedPlayer.sendMessage(message);
                    }
                } else {
                    loopedPlayer.sendMessage(message);
                }
            }
        }
    }
}
