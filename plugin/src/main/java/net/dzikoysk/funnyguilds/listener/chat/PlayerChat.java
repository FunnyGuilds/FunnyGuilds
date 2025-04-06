package net.dzikoysk.funnyguilds.listener.chat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildChatEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildPreChatEvent;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;
import panda.std.stream.PandaStream;

public abstract class PlayerChat extends AbstractFunnyListener {

    @Inject
    protected GuildManager guildManager;

    abstract protected boolean shouldHandleGuildChat();

    protected FunnyFormatter buildChatMessageFormatter(User user) {
        int points = user.getRank().getPoints();
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{RANK}", ChatUtils.deserializeSection(this.config.chatRank.getValue()))
                .register("{RANK}", user.getRank().getPosition(DefaultTops.USER_POINTS_TOP))
                .register("{POINTS}", ChatUtils.deserializeSection(this.config.chatPoints.getValue()))
                .register("{POINTS-FORMAT}", ChatUtils.deserializeSection(NumberRange.inRangeToString(points, this.config.pointsFormat)))
                .register("{POINTS}", points);

        user.getGuild()
                .peek(guild -> {
                    formatter.register("{TAG}", ChatUtils.deserializeSection(this.config.chatGuild.getValue()));
                    formatter.register("{TAG}", guild.getTag());
                    formatter.register("{POS}", ChatUtils.deserializeSection(this.config.chatPosition.getValue()));
                    formatter.register("{POS}", UserUtils.getUserPosition(this.config, user));
                })
                .onEmpty(() -> {
                    formatter.register("{TAG}", "");
                    formatter.register("{POS}", "");
                });

        return formatter;
    }

    protected boolean handleGuildChat(Player player, User user, String message) {
        if (this.shouldHandleGuildChat()) {
            return false;
        }

        if (!this.sendGuildMessage(user, player, message)) {
            return false;
        }

        if (this.config.logGuildChat) {
            FunnyGuilds.getPluginLogger().info("[Guild Chat] " + player.getName() + ": " + message);
        }

        return true;
    }

    private boolean sendGuildMessage(User user, Player player, String message) {
        Option<Guild> guildOption = user.getGuild();
        if (guildOption.isEmpty()) {
            return false;
        }

        if (this.sendMessageToAllGuilds(user, player, guildOption.get(), message)) {
            return true;
        }

        if (this.sendMessageToGuildAllies(user, player, guildOption.get(), message)) {
            return true;
        }

        return this.sendMessageToGuildMembers(user, player, guildOption.get(), message);
    }

    private boolean sendMessageToGuildMembers(User user, Player player, Guild guild, String message) {
        return this.sendMessageToGuilds(
                user,
                player,
                guild,
                this.config.chatPrivDesign.getValue(),
                this.config.chatPriv,
                message,
                Collections.singleton(guild),
                GuildChatEvent.Type.PRIVATE
        );
    }

    private boolean sendMessageToGuildAllies(User user, Player player, Guild guild, String message) {
        Set<Guild> allies = new HashSet<>(guild.getAllies());
        allies.add(guild);

        return this.sendMessageToGuilds(
                user,
                player,
                guild,
                this.config.chatAllyDesign.getValue(),
                this.config.chatAlly,
                message,
                allies,
                GuildChatEvent.Type.ALLY
        );
    }

    private boolean sendMessageToAllGuilds(User user, Player player, Guild guild, String message) {
        return this.sendMessageToGuilds(
                user,
                player,
                guild,
                this.config.chatGlobalDesign.getValue(),
                this.config.chatGlobal,
                message,
                this.guildManager.getGuilds(),
                GuildChatEvent.Type.ALL
        );
    }

    private boolean sendMessageToGuilds(
            User user,
            Player player,
            Guild playerGuild,
            String chatDesign,
            String prefix,
            String message,
            Set<Guild> receivers,
            GuildChatEvent.Type type
    ) {
        int prefixLength = prefix.length();

        if (message.length() > prefixLength && message.substring(0, prefixLength).equalsIgnoreCase(prefix)) {
            String subMessage = message.substring(prefixLength).trim();
            String resultMessage = this.formatChatDesign(user, player, playerGuild, chatDesign, subMessage);

            GuildPreChatEvent preChatEvent = new GuildPreChatEvent(FunnyEvent.EventCause.USER, user, playerGuild, type, receivers, resultMessage);
            if (!SimpleEventHandler.handle(preChatEvent)) {
                return true;
            }

            this.spy(user, player, playerGuild, subMessage);
            preChatEvent.getReceivers().forEach(guild -> sendMessageToGuild(guild, resultMessage));

            SimpleEventHandler.handle(new GuildChatEvent(FunnyEvent.EventCause.USER, user, playerGuild, type, receivers, resultMessage));
            return true;
        }

        return false;
    }

    private static void sendMessageToGuild(Guild guild, String message) {
        PandaStream.of(guild.getMembers())
                .filterNot(member -> member.getCache().isSpy())
                .forEach(member -> member.sendMessage(message));
    }

    private void spy(User user, Player player, Guild playerGuild, String message) {
        String spyMessage = this.formatChatDesign(user, player, playerGuild, this.config.chatSpyDesign.getValue(), message);
        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(onlinePlayer -> this.userManager.findByPlayer(onlinePlayer))
                .filter(onlineUser -> onlineUser.getCache().isSpy())
                .forEach(onlineUser -> onlineUser.sendMessage(spyMessage));
    }

    private String formatChatDesign(User user, Player player, Guild playerGuild, String chatDesign, String message) {
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", player.getName())
                .register("{TAG}", playerGuild.getTag())
                .register("{POS}", this.config.chatPosition.getValue())
                .register("{POS}", UserUtils.getUserPosition(this.config, user))
                .register("{MESSAGE}", message);

        return HookUtils.replacePlaceholders(player, formatter.replace(chatDesign));
    }
}
