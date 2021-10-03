package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionEnterEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionLeaveEvent;
import net.dzikoysk.funnyguilds.feature.notification.NotificationStyle;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import panda.utilities.text.Formatter;

public class PlayerMove implements Listener {

    private final FunnyGuilds plugin;

    public PlayerMove(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        onMove(event);
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        PluginConfiguration config = plugin.getPluginConfiguration();
        MessageConfiguration messages = plugin.getMessageConfiguration();

        Location from = event.getFrom();
        Location to = event.getTo();
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (to == null) {
                return;
            }

            if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
                return;
            }

            User user = UserUtils.get(player.getUniqueId());

            if (user == null) {
                return;
            }

            UserCache cache = user.getCache();
            Region region = RegionUtils.getAt(to);
            
            if (region == null && user.getCache().getEnter()) {
                cache.setEnter(false);
                region = RegionUtils.getAt(from);

                if (region != null) {
                    Guild guild = region.getGuild();

                    if (! SimpleEventHandler.handle(new GuildRegionLeaveEvent(EventCause.USER, user, guild))) {
                        event.setCancelled(true);
                        return;
                    }

                    if (config.heart.createEntityType != null) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> GuildEntityHelper.despawnGuildHeart(guild, player), 40L);
                    }

                    Formatter formatter = new Formatter()
                                    .register("{GUILD}", guild.getName())
                                    .register("{TAG}", guild.getTag());
                    
                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                        plugin.getNmsAccessor().getMessageAccessor()
                                .sendActionBarMessage(formatter.format(messages.notificationActionbarLeaveGuildRegion), player);
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.BOSSBAR)) {
                        user.getBossBar().sendNotification(
                                formatter.format(messages.notificationBossbarLeaveGuildRegion),
                                config.bossBarOptions_,
                                config.regionNotificationTime
                        );
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.CHAT)) {
                        player.sendMessage(formatter.format(messages.notificationChatLeaveGuildRegion));
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.TITLE)) {
                        TitleMessage titleMessage = TitleMessage.builder()
                                .text(formatter.format(messages.notificationTitleLeaveGuildRegion))
                                .subText(formatter.format(messages.notificationSubtitleLeaveGuildRegion))
                                .fadeInDuration(config.notificationTitleFadeIn)
                                .stayDuration(config.notificationTitleStay)
                                .fadeOutDuration(config.notificationTitleFadeOut)
                                .build();

                        plugin.getNmsAccessor().getMessageAccessor().sendTitleMessage(titleMessage, player);
                    }
                }
            }
            else if (!cache.getEnter() && region != null) {
                Guild guild = region.getGuild();

                if (guild == null || guild.getName() == null) {
                    return;
                }

                if (! SimpleEventHandler.handle(new GuildRegionEnterEvent(EventCause.USER, user, guild))) {
                    event.setCancelled(true);
                    return;
                }

                cache.setEnter(true);

                if (config.heart.createEntityType != null) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> GuildEntityHelper.spawnGuildHeart(guild, player), 40L);
                }

                Formatter formatter = new Formatter()
                                .register("{GUILD}", guild.getName())
                                .register("{TAG}", guild.getTag())
                                .register("{PLAYER}", player.getName());

                if (config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                    plugin.getNmsAccessor().getMessageAccessor()
                            .sendActionBarMessage(formatter.format(messages.notificationActionbarEnterGuildRegion), player);
                }

                if (config.regionEnterNotificationStyle.contains(NotificationStyle.BOSSBAR)) {
                    user.getBossBar().sendNotification(
                            formatter.format(messages.notificationBossbarEnterGuildRegion),
                            config.bossBarOptions_,
                            config.regionNotificationTime
                    );
                }

                if (config.regionEnterNotificationStyle.contains(NotificationStyle.CHAT)) {
                    player.sendMessage(formatter.format(messages.notificationChatEnterGuildRegion));
                }

                if (config.regionEnterNotificationStyle.contains(NotificationStyle.TITLE)) {
                    TitleMessage titleMessage = TitleMessage.builder()
                            .text(formatter.format(messages.notificationTitleEnterGuildRegion))
                            .subText(formatter.format(messages.notificationSubtitleEnterGuildRegion))
                            .fadeInDuration(config.notificationTitleFadeIn)
                            .stayDuration(config.notificationTitleStay)
                            .fadeOutDuration(config.notificationTitleFadeOut)
                            .build();

                    plugin.getNmsAccessor().getMessageAccessor().sendTitleMessage(titleMessage, player);
                }

                if (player.hasPermission("funnyguilds.admin.notification")) {
                    return;
                }

                if (cache.getNotificationTime() > 0 && System.currentTimeMillis() < cache.getNotificationTime()) {
                    return;
                }

                if (!config.regionEnterNotificationGuildMember && user.getGuild() != null && guild.getTag().equals(user.getGuild().getTag())) {
                    return;
                }

                for (User memberUser : guild.getOnlineMembers()) {
                    if (memberUser == null) {
                        continue;
                    }

                    Player member = memberUser.getPlayer();

                    if (member == null || !member.isOnline()) {
                        continue;
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                        plugin.getNmsAccessor().getMessageAccessor()
                                .sendActionBarMessage(formatter.format(messages.notificationActionbarIntruderEnterGuildRegion), member);
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.BOSSBAR)) {
                        memberUser.getBossBar().sendNotification(
                                formatter.format(messages.notificationBossbarIntruderEnterGuildRegion),
                                config.bossBarOptions_,
                                config.regionNotificationTime
                        );
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.CHAT)) {
                        member.sendMessage(formatter.format(messages.notificationChatIntruderEnterGuildRegion));
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.TITLE)) {
                        TitleMessage titleMessage = TitleMessage.builder()
                                .text(formatter.format(messages.notificationTitleIntruderEnterGuildRegion))
                                .subText(formatter.format(messages.notificationSubtitleIntruderEnterGuildRegion))
                                .fadeInDuration(config.notificationTitleFadeIn)
                                .stayDuration(config.notificationTitleStay)
                                .fadeOutDuration(config.notificationTitleFadeOut)
                                .build();

                        plugin.getNmsAccessor().getMessageAccessor().sendTitleMessage(titleMessage, member);
                    }
                }

                cache.setNotificationTime(System.currentTimeMillis() + 1000L * config.regionNotificationCooldown);
            }
        });
    }

}
