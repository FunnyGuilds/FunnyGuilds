package net.dzikoysk.funnyguilds.listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildChatEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildChatEvent.Type;
import net.dzikoysk.funnyguilds.event.guild.GuildPreChatEvent;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.permission.GenericGuildPermissions;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionController;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class PlayerChat extends AbstractFunnyListener {

    @Inject
    private GuildManager guildManager;
    
    @Inject
    private GuildPermissionController permissionController;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Option<User> userOption = this.userManager.findByPlayer(player);
        if (userOption.isEmpty()) {
            return;
        }

        User user = userOption.get();
        boolean isGuildChat = user.getGuild()
                .map(guild -> this.sendGuildMessage(user, player, guild, event.getMessage()))
                .orElseGet(false);

        if (isGuildChat) {
            event.setCancelled(true);

            if (this.config.logGuildChat) {
                FunnyGuilds.getPluginLogger().info("[Guild Chat] " + player.getName() + ": " + event.getMessage());
            }

            return;
        }

        int points = user.getRank().getPoints();
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{RANK}", this.config.chatRank.getValue())
                .register("{RANK}", user.getRank().getPosition(DefaultTops.USER_POINTS_TOP))
                .register("{POINTS}", this.config.chatPoints.getValue())
                .register("{POINTS-FORMAT}", NumberRange.inRangeToString(points, this.config.pointsFormat))
                .register("{POINTS}", points);

        user.getGuild()
                .peek(guild -> {
                    formatter.register("{TAG}", this.config.chatGuild.getValue());
                    formatter.register("{TAG}", guild.getTag());
                    formatter.register("{POS}", this.config.chatPosition.getValue());
                    formatter.register("{POS}", UserUtils.getUserPosition(this.permissionController, user));
                })
                .onEmpty(() -> {
                    formatter.register("{TAG}", "");
                    formatter.register("{POS}", "");
                });

        event.setFormat(formatter.format(event.getFormat()));
    }

    private boolean sendGuildMessage(User user, Player player, Guild guild, String message) {
        if (this.sendMessageToAllGuilds(user, player, guild, message)) {
            return true;
        }

        if (this.sendMessageToGuildAllies(user, player, guild, message)) {
            return true;
        }

        return this.sendMessageToGuildMembers(user, player, guild, message);
    }

    private boolean sendMessageToGuildMembers(User user, Player player, Guild guild, String message) {
        return this.sendMessageToGuilds(user, player, guild, this.config.chatPrivDesign.getValue(), this.config.chatPriv,
                message, Collections.singleton(guild), Type.PRIVATE);
    }

    private boolean sendMessageToGuildAllies(User user, Player player, Guild guild, String message) {
        Set<Guild> allies = new HashSet<>(guild.getAllies());
        allies.add(guild);

        return this.sendMessageToGuilds(user, player, guild, this.config.chatAllyDesign.getValue(), this.config.chatAlly,
                message, allies, Type.ALLY);
    }

    private boolean sendMessageToAllGuilds(User user, Player player, Guild guild, String message) {
        return this.sendMessageToGuilds(user, player, guild, this.config.chatGlobalDesign.getValue(), this.config.chatGlobal,
                message, this.guildManager.getGuilds(), Type.ALL);
    }

    private boolean sendMessageToGuilds(User user, Player player, Guild playerGuild, String chatDesign, String prefix, String message,
                                        Set<Guild> receivers, Type type) {
        int prefixLength = prefix.length();

        if (message.length() > prefixLength && message.substring(0, prefixLength).equalsIgnoreCase(prefix)) {
            if (!this.checkUsePermission(user, playerGuild, type)) {
                // TODO: send custom message when no permission
                return true;
            }
            
            String subMessage = message.substring(prefixLength).trim();
            String resultMessage = this.formatChatDesign(user, player, playerGuild, chatDesign, subMessage);

            GuildPreChatEvent preChatEvent = new GuildPreChatEvent(EventCause.USER, user, playerGuild, type, receivers, resultMessage);
            if (!SimpleEventHandler.handle(preChatEvent)) {
                return true;
            }

            this.spy(user, player, playerGuild, subMessage);
            preChatEvent.getReceivers().forEach(guild -> sendMessageToGuild(guild, resultMessage, type));

            SimpleEventHandler.handle(new GuildChatEvent(EventCause.USER, user, playerGuild, type, receivers, resultMessage));

            return true;
        }

        return false;
    }

    private void sendMessageToGuild(Guild guild, String message, Type type) {
        PandaStream.of(guild.getMembers())
                .filterNot(member -> member.getCache().isSpy())
                .filter(member -> this.checkSeePermission(member, guild, type) || this.checkUsePermission(member, guild, type))
                .forEach(member -> member.sendMessage(message));
    }
    
    private boolean checkSeePermission(User user, Guild guild, Type type) {
        GuildPermission<Boolean> seePermission;
        switch (type) {
            case PRIVATE:
                seePermission = GenericGuildPermissions.GUILD_CHAT_SEE;
                break;
            case ALLY:
                seePermission = GenericGuildPermissions.ALLY_CHAT_SEE;
                break;
            case ALL:
                seePermission = GenericGuildPermissions.GLOBAL_CHAT_SEE;
                break;
            default:
                return false;
        }
        return this.permissionController.getPermissionValue(guild, user, seePermission)
                .orElseGet(true);
    }
    
    private boolean checkUsePermission(User user, Guild guild, Type type) {
        GuildPermission<Boolean> usePermission;
        switch (type) {
            case PRIVATE:
                usePermission = GenericGuildPermissions.GUILD_CHAT_USE;
                break;
            case ALLY:
                usePermission = GenericGuildPermissions.ALLY_CHAT_USE;
                break;
            case ALL:
                usePermission = GenericGuildPermissions.GLOBAL_CHAT_USE;
                break;
            default:
                return false;
        }
        return this.permissionController.getPermissionValue(guild, user, usePermission)
                .orElseGet(true);
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
                .register("{POS}", UserUtils.getUserPosition(this.permissionController, user))
                .register("{MESSAGE}", message);

        return HookUtils.replacePlaceholders(player, formatter.format(chatDesign));
    }

}
