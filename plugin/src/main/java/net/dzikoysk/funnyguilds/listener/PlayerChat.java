package net.dzikoysk.funnyguilds.listener;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class PlayerChat extends AbstractFunnyListener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Option<User> userOption = userManager.findByPlayer(player);
        if (userOption.isEmpty()) {
            return;
        }
        User user = userOption.get();

        boolean guildChatHandled = user.getGuild()
                .map(guild -> {
                    String message = event.getMessage();

                    if (sendGuildMessage(player, guild, message)) {
                        event.setCancelled(true);

                        if (config.logGuildChat) {
                            FunnyGuilds.getPluginLogger().info("[Guild Chat] " + message);
                        }

                        return true;
                    }

                    return false;
                })
                .orElseGet(false);

        if (guildChatHandled) {
            return;
        }

        int points = user.getRank().getPoints();
        String format = event.getFormat();

        format = StringUtils.replace(format, "{RANK}", StringUtils.replace(config.chatRank.getValue(), "{RANK}", String.valueOf(user.getRank().getPosition(DefaultTops.USER_POINTS_TOP))));
        format = StringUtils.replace(format, "{POINTS}", config.chatPoints.getValue());
        format = StringUtils.replace(format, "{POINTS-FORMAT}", NumberRange.inRangeToString(points, config.pointsFormat));
        format = StringUtils.replace(format, "{POINTS}", String.valueOf(points));

        if (user.hasGuild()) {
            format = StringUtils.replace(format, "{TAG}", StringUtils.replace(config.chatGuild.getValue(), "{TAG}", user.getGuild().get().getTag()));
            format = StringUtils.replace(format, "{POS}", StringUtils.replace(config.chatPosition.getValue(), "{POS}", getUserPosition(this.userManager.findByPlayer(player))));
        }
        else {
            format = StringUtils.replace(format, "{TAG}", "");
            format = StringUtils.replace(format, "{POS}", "");
        }

        event.setFormat(format);
    }

    private boolean sendGuildMessage(Player player, Guild guild, String message) {
        if (sendMessageToAllGuilds(player, guild, message)) {
            return true;
        }

        if (sendMessageToGuildAllies(player, guild, message)) {
            return true;
        }

        return sendMessageToGuildMembers(player, guild, message);
    }

    private void spy(Player player, String message) {
        String spyMessage = ChatColor.GOLD + "[Spy] " + ChatColor.GRAY + player.getName() + ": " + ChatColor.WHITE + message;

        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(onlinePlayer -> userManager.findByPlayer(onlinePlayer))
                .filter(user -> user.getCache().isSpy())
                .forEach(user -> user.sendMessage(spyMessage));
    }

    private boolean sendMessageToGuildMembers(Player player, Guild guild, String message) {
        return this.sendMessageToGuilds(player, guild, config.chatPrivDesign.getValue(), config.chatPriv, message, Collections.singletonList(guild));
    }

    private boolean sendMessageToGuildAllies(Player player, Guild guild, String message) {
        Set<Guild> allies = new HashSet<>(guild.getAllies());
        allies.add(guild);
        return this.sendMessageToGuilds(player, guild, config.chatAllyDesign.getValue(), config.chatAlly, message, allies);
    }

    private boolean sendMessageToAllGuilds(Player player, Guild guild, String message) {
        return this.sendMessageToGuilds(player, guild, config.chatGlobalDesign.getValue(), config.chatGlobal, message, this.guildManager.getGuilds());
    }

    private boolean sendMessageToGuilds(Player player, Guild playerGuild, String chatDesign, String prefix, String message, Collection<Guild> receivers) {
        int prefixLength = prefix.length();

        if (message.length() > prefixLength && message.substring(0, prefixLength).equalsIgnoreCase(prefix)) {
            String resultMessage = chatDesign;

            resultMessage = StringUtils.replace(resultMessage, "{PLAYER}", player.getName());
            resultMessage = StringUtils.replace(resultMessage, "{TAG}", playerGuild.getTag());
            resultMessage = StringUtils.replace(resultMessage, "{POS}",
                    StringUtils.replace(config.chatPosition.getValue(), "{POS}", getUserPosition(this.userManager.findByUuid(player.getUniqueId()))));

            resultMessage = HookUtils.replacePlaceholders(player, resultMessage);

            String subMessage = message.substring(prefixLength).trim();
            resultMessage = StringUtils.replace(resultMessage, "{MESSAGE}", subMessage);

            this.spy(player, subMessage);

            String finalResultMessage = resultMessage;
            receivers.forEach(guild -> this.sendMessageToGuild(guild, finalResultMessage));

            return true;
        }

        return false;
    }

    private void sendMessageToGuild(Guild guild, String message) {
        PandaStream.of(guild.getMembers())
                .filterNot(member -> member.getCache().isSpy())
                .forEach(member -> member.sendMessage(message));
    }

    private String getUserPosition(Option<User> userOption) {
        if (userOption.isEmpty()) {
            return "";
        }
        User user = userOption.get();

        if (user.isOwner()) {
            return config.chatPositionLeader;
        }

        if (user.isDeputy()) {
            return config.chatPositionDeputy;
        }

        return config.chatPositionMember;
    }

}
