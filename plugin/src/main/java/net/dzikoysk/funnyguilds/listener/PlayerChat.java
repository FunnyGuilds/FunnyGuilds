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
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import panda.std.Option;
import panda.std.stream.PandaStream;
import panda.utilities.text.Formatter;

public class PlayerChat extends AbstractFunnyListener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Option<User> userOption = userManager.findByPlayer(player);
        if (userOption.isEmpty()) {
            return;
        }
        User user = userOption.get();

        boolean isGuildChat = user.getGuild()
                .map(guild -> this.sendGuildMessage(player, guild, event.getMessage()))
                .orElseGet(false);

        if (isGuildChat) {
            event.setCancelled(true);

            if (config.logGuildChat) {
                FunnyGuilds.getPluginLogger().info("[Guild Chat] " + player.getName() + ": "+ event.getMessage());
            }

            return;
        }

        int points = user.getRank().getPoints();

        Formatter formatter = new Formatter()
                .register("{RANK}", config.chatRank.getValue().replace("{RANK}", String.valueOf(user.getRank().getPosition(DefaultTops.USER_POINTS_TOP))))
                .register("{POINTS}", config.chatPoints.getValue())
                .register("{POINTS-FORMAT}", NumberRange.inRangeToString(points, config.pointsFormat))
                .register("{POINTS}", String.valueOf(points));

        user.getGuild()
                .peek(guild -> {
                    formatter.register("{TAG}", config.chatGuild.getValue().replace("{TAG}", guild.getTag()));
                    formatter.register("{POS}", config.chatPosition.getValue().replace("{POS}", UserUtils.getUserPosition(config, user)));
                })
                .onEmpty(() -> {
                    formatter.register("{TAG}", "");
                    formatter.register("{POS}", "");
                });

        String format = formatter.format(event.getFormat());

        event.setFormat(format);
    }

    private boolean sendGuildMessage(Player player, Guild guild, String message) {
        if (this.sendMessageToAllGuilds(player, guild, message)) {
            return true;
        }

        if (this.sendMessageToGuildAllies(player, guild, message)) {
            return true;
        }

        return this.sendMessageToGuildMembers(player, guild, message);
    }

    private void spy(Player player, Guild playerGuild, String message) {
        String spyMessage = this.formatChatDesign(player, playerGuild, config.chatSpyDesign.getValue(), message);

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
            String subMessage = message.substring(prefixLength).trim();
            String resultMessage = this.formatChatDesign(player, playerGuild, chatDesign, subMessage);

            this.spy(player, playerGuild, subMessage);

            receivers.forEach(guild -> this.sendMessageToGuild(guild, resultMessage));

            return true;
        }

        return false;
    }

    private void sendMessageToGuild(Guild guild, String message) {
        PandaStream.of(guild.getMembers())
                .filterNot(member -> member.getCache().isSpy())
                .forEach(member -> member.sendMessage(message));
    }

    private String formatChatDesign(Player player, Guild playerGuild, String chatDesign, String message) {
        String resultMessage = chatDesign;

        Formatter formatter = new Formatter()
                .register("{PLAYER}", player.getName())
                .register("{TAG}", playerGuild.getTag())
                .register("{POS}", config.chatPosition.replace("{POS}", UserUtils.getUserPosition(config, this.userManager.findByUuid(player.getUniqueId()).orNull())))
                .register("{MESSAGE}", message);
        resultMessage = formatter.format(resultMessage);

        resultMessage = HookUtils.replacePlaceholders(player, resultMessage);

        return resultMessage;
    }

}
