package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionEnterEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionLeaveEvent;
import net.dzikoysk.funnyguilds.feature.notification.NotificationStyle;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import panda.std.Option;

public class PlayerMove extends AbstractFunnyListener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        this.userManager.findByUuid(event.getPlayer().getUniqueId())
                .map(User::getCache)
                .peek(userCache -> userCache.setEnter(false));

        this.onMove(event);
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

            Option<Region> regionOption = this.regionManager.findRegionAtLocation(to);
            if (regionOption.isEmpty() && user.getCache().getEnter()) {
                cache.setEnter(false);

                this.regionManager.findRegionAtLocation(from)
                        .map(Region::getGuild)
                        .peek(guild -> {
                            if (!SimpleEventHandler.handle(new GuildRegionLeaveEvent(EventCause.USER, user, guild))) {
                                event.setCancelled(true);
                                return;
                            }

                            FunnyFormatter formatter = new FunnyFormatter()
                                    .register("{GUILD}", guild.getName())
                                    .register("{TAG}", guild.getTag());

                            if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                                this.messageAccessor.sendActionBarMessage(formatter.format(this.messages.notificationActionbarLeaveGuildRegion), player);
                            }

                            if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.BOSSBAR)) {
                                this.bossBarService.getBossBarProvider(user).sendNotification(
                                        formatter.format(this.messages.notificationBossbarLeaveGuildRegion),
                                        this.config.bossBarOptions_,
                                        this.config.regionNotificationTime
                                );
                            }

                            if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.CHAT)) {
                                ChatUtils.sendMessage(player, formatter.format(this.messages.notificationChatLeaveGuildRegion));
                            }

                            if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.TITLE)) {
                                TitleMessage titleMessage = TitleMessage.builder()
                                        .text(formatter.format(this.messages.notificationTitleLeaveGuildRegion))
                                        .subText(formatter.format(this.messages.notificationSubtitleLeaveGuildRegion))
                                        .fadeInDuration(this.config.notificationTitleFadeIn)
                                        .stayDuration(this.config.notificationTitleStay)
                                        .fadeOutDuration(this.config.notificationTitleFadeOut)
                                        .build();

                                this.messageAccessor.sendTitleMessage(titleMessage, player);
                            }
                        });
            }
            else if (!cache.getEnter()) {
                regionOption.map(Region::getGuild)
                        .peek(guild -> {
                            if (guild.getName() == null) {
                                return;
                            }

                            if (!SimpleEventHandler.handle(new GuildRegionEnterEvent(EventCause.USER, user, guild))) {
                                event.setCancelled(true);
                                return;
                            }

                            cache.setEnter(true);

                            if (this.config.heart.createEntityType != null) {
                                Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
                                    this.guildEntityHelper.spawnGuildEntity(guild, player);
                                }, 40L);
                            }

                            FunnyFormatter formatter = new FunnyFormatter()
                                    .register("{GUILD}", guild.getName())
                                    .register("{TAG}", guild.getTag())
                                    .register("{PLAYER}", player.getName());

                            if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                                this.messageAccessor.sendActionBarMessage(formatter.format(this.messages.notificationActionbarEnterGuildRegion), player);
                            }

                            if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.BOSSBAR)) {
                                this.bossBarService.getBossBarProvider(user).sendNotification(
                                        formatter.format(this.messages.notificationBossbarEnterGuildRegion),
                                        this.config.bossBarOptions_,
                                        this.config.regionNotificationTime
                                );
                            }

                            if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.CHAT)) {
                                user.sendMessage(formatter.format(this.messages.notificationChatEnterGuildRegion));
                            }

                            if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.TITLE)) {
                                TitleMessage titleMessage = TitleMessage.builder()
                                        .text(formatter.format(this.messages.notificationTitleEnterGuildRegion))
                                        .subText(formatter.format(this.messages.notificationSubtitleEnterGuildRegion))
                                        .fadeInDuration(this.config.notificationTitleFadeIn)
                                        .stayDuration(this.config.notificationTitleStay)
                                        .fadeOutDuration(this.config.notificationTitleFadeOut)
                                        .build();

                                this.messageAccessor.sendTitleMessage(titleMessage, player);
                            }

                            if (player.hasPermission("funnyguilds.admin.notification")) {
                                return;
                            }

                            if (cache.getNotificationTime() > 0 && System.currentTimeMillis() < cache.getNotificationTime()) {
                                return;
                            }

                            if (!this.config.regionEnterNotificationGuildMember && user.hasGuild() &&
                                    guild.getTag().equals(user.getGuild().get().getTag())) {
                                return;
                            }

                            for (User memberUser : guild.getOnlineMembers()) {
                                Player memberPlayer = this.server.getPlayer(memberUser.getUUID());
                                if (memberPlayer == null) {
                                    continue;
                                }

                                if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                                    this.messageAccessor.sendActionBarMessage(formatter.format(
                                            this.messages.notificationActionbarIntruderEnterGuildRegion), memberPlayer
                                    );
                                }

                                if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.BOSSBAR)) {
                                    this.bossBarService.getBossBarProvider(memberUser).sendNotification(
                                            formatter.format(this.messages.notificationBossbarIntruderEnterGuildRegion),
                                            this.config.bossBarOptions_,
                                            this.config.regionNotificationTime
                                    );
                                }

                                if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.CHAT)) {
                                    memberUser.sendMessage(formatter.format(this.messages.notificationChatIntruderEnterGuildRegion));
                                }

                                if (this.config.regionEnterNotificationStyle.contains(NotificationStyle.TITLE)) {
                                    TitleMessage titleMessage = TitleMessage.builder()
                                            .text(formatter.format(this.messages.notificationTitleIntruderEnterGuildRegion))
                                            .subText(formatter.format(this.messages.notificationSubtitleIntruderEnterGuildRegion))
                                            .fadeInDuration(this.config.notificationTitleFadeIn)
                                            .stayDuration(this.config.notificationTitleStay)
                                            .fadeOutDuration(this.config.notificationTitleFadeOut)
                                            .build();

                                    this.messageAccessor.sendTitleMessage(titleMessage, memberPlayer);
                                }

                            }

                            cache.setNotificationTime(System.currentTimeMillis() + 1000L * this.config.regionNotificationCooldown);
                        });
            }
        });
    }

}
