package net.dzikoysk.funnyguilds.listener;

import javax.annotation.Nullable;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.range.IntegerRange;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import panda.std.Option;

public class PlayerChat extends AbstractFunnyListener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Option<User> userOption = userManager.findByPlayer(player);
        if (userOption.isEmpty()) {
            return;
        }
        User user = userOption.get();

        if (user.hasGuild()) {
            Guild guild = user.getGuild();
            String message = event.getMessage();

            if (sendGuildMessage(event, message, player, guild)) {
                if (config.logGuildChat) {
                    FunnyGuilds.getPluginLogger().info("[Guild Chat] " + message);
                }

                return;
            }
        }

        int points = user.getRank().getPoints();
        String format = event.getFormat();

        format = StringUtils.replace(format, "{RANK}", StringUtils.replace(config.chatRank.getValue(), "{RANK}", String.valueOf(user.getRank().getPosition())));
        format = StringUtils.replace(format, "{POINTS}", config.chatPoints.getValue());
        format = StringUtils.replace(format, "{POINTS-FORMAT}", IntegerRange.inRangeToString(points, config.pointsFormat));
        format = StringUtils.replace(format, "{POINTS}", String.valueOf(points));

        if (user.hasGuild()) {
            format = StringUtils.replace(format, "{TAG}", StringUtils.replace(config.chatGuild.getValue(), "{TAG}", user.getGuild().getTag()));
            format = StringUtils.replace(format, "{POS}", StringUtils.replace(config.chatPosition.getValue(), "{POS}", getPositionString(user, config)));
        }
        else {
            format = StringUtils.replace(format, "{TAG}", "");
            format = StringUtils.replace(format, "{POS}", "");
        }

        event.setFormat(format);
    }

    private boolean sendGuildMessage(AsyncPlayerChatEvent event, String message, Player player, Guild guild) {
        if (sendMessageToAllGuilds(event, message, config, player, guild)) {
            return true;
        }

        if (sendMessageToGuildAllies(event, message, config, player, guild)) {
            return true;
        }

        return sendMessageToGuildMembers(event, message, config, player, guild);
    }

    private void spy(Player player, String message) {
        String spyMessage = ChatColor.GOLD + "[Spy] " + ChatColor.GRAY + player.getName() + ": " + ChatColor.WHITE + message;

        for (Player looped : Bukkit.getOnlinePlayers()) {
            this.userManager.findByPlayer(looped)
                    .filter(user -> user.getCache().isSpy())
                    .peek(user -> {
                        looped.sendMessage(spyMessage);
                    });
        }
    }

    private boolean sendMessageToGuildMembers(AsyncPlayerChatEvent event, String message, PluginConfiguration config, Player player, Guild guild) {
        String guildPrefix = config.chatPriv;
        int prefixLength = guildPrefix.length();

        if (message.length() > prefixLength && message.substring(0, prefixLength).equals(guildPrefix)) {
            String resultMessage = config.chatPrivDesign.getValue();

            resultMessage = StringUtils.replace(resultMessage, "{PLAYER}", player.getName());
            resultMessage = StringUtils.replace(resultMessage, "{TAG}", guild.getTag());
            resultMessage = StringUtils.replace(resultMessage, "{POS}",
                    StringUtils.replace(config.chatPosition.getValue(), "{POS}", getPositionString(UserUtils.get(player.getUniqueId()), config)));

            String messageWithoutPrefix = event.getMessage().substring(prefixLength).trim();
            resultMessage = StringUtils.replace(resultMessage, "{MESSAGE}", messageWithoutPrefix);

            this.spy(player, messageWithoutPrefix);
            this.sendMessageToGuild(guild, player, resultMessage);

            event.setCancelled(true);
            return true;
        }

        return false;
    }

    private boolean sendMessageToGuildAllies(AsyncPlayerChatEvent event, String message, PluginConfiguration config, Player player, Guild guild) {
        String allyPrefix = config.chatAlly;
        int prefixLength = allyPrefix.length();

        if (message.length() > prefixLength && message.substring(0, prefixLength).equals(allyPrefix)) {
            String resultMessage = config.chatAllyDesign.getValue();

            resultMessage = StringUtils.replace(resultMessage, "{PLAYER}", player.getName());
            resultMessage = StringUtils.replace(resultMessage, "{TAG}", guild.getTag());
            resultMessage = StringUtils.replace(resultMessage, "{POS}",
                    StringUtils.replace(config.chatPosition.getValue(), "{POS}", getPositionString(UserUtils.get(player.getUniqueId()), config)));

            String subMessage = event.getMessage().substring(prefixLength).trim();
            resultMessage = StringUtils.replace(resultMessage, "{MESSAGE}", subMessage);

            this.spy(player, subMessage);
            this.sendMessageToGuild(guild, player, resultMessage);

            for (Guild ally : guild.getAllies()) {
                this.sendMessageToGuild(ally, player, resultMessage);
            }

            event.setCancelled(true);
            return true;
        }

        return false;
    }

    private boolean sendMessageToAllGuilds(AsyncPlayerChatEvent event, String message, PluginConfiguration config, Player player, Guild guild) {
        String allGuildsPrefix = config.chatGlobal;
        int prefixLength = allGuildsPrefix.length();

        if (message.length() > prefixLength && message.substring(0, prefixLength).equals(allGuildsPrefix)) {
            String resultMessage = config.chatGlobalDesign.getValue();

            resultMessage = StringUtils.replace(resultMessage, "{PLAYER}", player.getName());
            resultMessage = StringUtils.replace(resultMessage, "{TAG}", guild.getTag());
            resultMessage = StringUtils.replace(resultMessage, "{POS}",
                    StringUtils.replace(config.chatPosition.getValue(), "{POS}", getPositionString(this.userManager.findByPlayer(player).getOrNull(), config)));

            String subMessage = event.getMessage().substring(prefixLength).trim();
            resultMessage = StringUtils.replace(resultMessage, "{MESSAGE}", subMessage);

            this.spy(player, subMessage);

            for (Guild globalGuild : this.guildManager.getGuilds()) {
                this.sendMessageToGuild(globalGuild, player, resultMessage);
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

        for (User onlineMember : guild.getOnlineMembers()) {
            Player member = onlineMember.getPlayer();

            if (member == null) {
                continue;
            }

            if (!member.equals(player) || !onlineMember.getCache().isSpy()) {
                member.sendMessage(message);
            }
        }
    }

    private String getPositionString(@Nullable User user, PluginConfiguration config) {
        if (user == null) {
            return "";
        }

        if (user.isOwner()) {
            return config.chatPositionLeader;
        }

        if (user.isDeputy()) {
            return config.chatPositionDeputy;
        }

        return config.chatPositionMember;
    }

}
