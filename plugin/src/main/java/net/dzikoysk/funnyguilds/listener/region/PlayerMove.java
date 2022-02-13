package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionEnterEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionLeaveEvent;
import net.dzikoysk.funnyguilds.feature.notification.NotificationStyle;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import panda.std.Option;
import panda.utilities.text.Formatter;

public class PlayerMove extends AbstractFunnyListener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        onMove(event);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        Player player = event.getPlayer();

        if (to == null) {
            return;
        }

        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {

            Option<User> userOption = this.userManager.findByPlayer(player);
            if (userOption.isEmpty()) {
                return;
            }
            User user = userOption.get();

            UserCache cache = user.getCache();

            Option<Region> regionToOption = this.regionManager.findRegionAtLocation(to);

            if (regionToOption.isEmpty() && user.getCache().getEnter()) {
                cache.setEnter(false);

                this.regionManager.findRegionAtLocation(from)
                        .flatMap(Region::getGuild)
                        .peek(guild -> {
                            if (!SimpleEventHandler.handle(new GuildRegionLeaveEvent(EventCause.USER, user, guild))) {
                                event.setCancelled(true);
                                return;
                            }

                            if (config.heart.createEntityType != null) {
                                Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> GuildEntityHelper.despawnGuildHeart(guild, player), 40L);
                            }

                            Formatter formatter = new Formatter()
                                    .register("{GUILD}", guild.getName())
                                    .register("{TAG}", guild.getTag());

                            if (config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                                this.plugin.getNmsAccessor().getMessageAccessor()
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
                        });
            }
            else if (!cache.getEnter()) {
                regionToOption.flatMap(Region::getGuild)
                        .peek(guild -> {
                            if (guild.getName() == null) {
                                return;
                            }

                            if (!SimpleEventHandler.handle(new GuildRegionEnterEvent(EventCause.USER, user, guild))) {
                                event.setCancelled(true);
                                return;
                            }

                            cache.setEnter(true);

                            if (config.heart.createEntityType != null) {
                                Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> GuildEntityHelper.spawnGuildHeart(guild, player), 40L);
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

                            if (!config.regionEnterNotificationGuildMember && user.hasGuild() && guild.getTag().equals(user.getGuildOption().get().getTag())) {
                                return;
                            }

                            for (User memberUser : guild.getOnlineMembers()) {
                                if (memberUser == null) {
                                    continue;
                                }

                                memberUser.getPlayer()
                                        .peek(peekPlayer -> {
                                            if (config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                                                plugin.getNmsAccessor().getMessageAccessor()
                                                        .sendActionBarMessage(formatter.format(messages.notificationActionbarIntruderEnterGuildRegion), peekPlayer);
                                            }

                                            if (config.regionEnterNotificationStyle.contains(NotificationStyle.BOSSBAR)) {
                                                memberUser.getBossBar().sendNotification(
                                                        formatter.format(messages.notificationBossbarIntruderEnterGuildRegion),
                                                        config.bossBarOptions_,
                                                        config.regionNotificationTime
                                                );
                                            }

                                            if (config.regionEnterNotificationStyle.contains(NotificationStyle.CHAT)) {
                                                peekPlayer.sendMessage(formatter.format(messages.notificationChatIntruderEnterGuildRegion));
                                            }

                                            if (config.regionEnterNotificationStyle.contains(NotificationStyle.TITLE)) {
                                                TitleMessage titleMessage = TitleMessage.builder()
                                                        .text(formatter.format(messages.notificationTitleIntruderEnterGuildRegion))
                                                        .subText(formatter.format(messages.notificationSubtitleIntruderEnterGuildRegion))
                                                        .fadeInDuration(config.notificationTitleFadeIn)
                                                        .stayDuration(config.notificationTitleStay)
                                                        .fadeOutDuration(config.notificationTitleFadeOut)
                                                        .build();

                                                this.plugin.getNmsAccessor().getMessageAccessor().sendTitleMessage(titleMessage, peekPlayer);
                                            }
                                        });
                            }

                            cache.setNotificationTime(System.currentTimeMillis() + 1000L * config.regionNotificationCooldown);
                        });
            }
        });
    }

}
