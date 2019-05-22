package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.element.notification.NotificationStyle;
import net.dzikoysk.funnyguilds.element.notification.NotificationUtil;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionEnterEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionLeaveEvent;
import net.dzikoysk.funnyguilds.util.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.util.nms.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.panda_lang.panda.utilities.commons.text.MessageFormatter;

public class PlayerMove implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        onMove(event);
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        final Player player = event.getPlayer();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        Bukkit.getScheduler().runTaskAsynchronously(FunnyGuilds.getInstance(), () -> {
            if (from == null || to == null) {
                return;
            }

            if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
                return;
            }

            User user = User.get(player);
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

                    FunnyGuilds.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(FunnyGuilds.getInstance(), () -> {
                        if (config.createEntityType != null) {
                            GuildEntityHelper.despawnGuildHeart(guild, player);
                        }
                    }, 40L);

                    MessageFormatter formatter = new MessageFormatter()
                                    .register("{GUILD}", guild.getName())
                                    .register("{TAG}", guild.getTag());
                    
                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                        PacketSender.sendPacket(player, NotificationUtil.createActionbarNotification(
                                        formatter.format(messages.notificationActionbarLeaveGuildRegion)));
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.BOSSBAR)) {
                        user.getBossBar().sendNotification(
                                formatter.format(messages.notificationActionbarLeaveGuildRegion),
                                config.bossBarOptions_,
                                config.regionNotificationTime
                        );
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.CHAT)) {
                        player.sendMessage(formatter.format(messages.notificationChatLeaveGuildRegion));
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.TITLE)) {
                        PacketSender.sendPacket(player, NotificationUtil.createTitleNotification(
                                        formatter.format(messages.notificationTitleLeaveGuildRegion),
                                        formatter.format(messages.notificationSubtitleLeaveGuildRegion),
                                        config.notificationTitleFadeIn, config.notificationTitleStay,
                                        config.notificationTitleFadeOut));
                    }
                }
            } else if (!cache.getEnter() && region != null) {
                Guild guild = region.getGuild();

                if (guild == null || guild.getName() == null) {
                    return;
                }

                if (! SimpleEventHandler.handle(new GuildRegionEnterEvent(EventCause.USER, user, guild))) {
                    event.setCancelled(true);
                    return;
                }

                cache.setEnter(true);

                FunnyGuilds.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(FunnyGuilds.getInstance(), () -> {
                    if (config.createEntityType != null) {
                        GuildEntityHelper.spawnGuildHeart(guild, player);
                    }
                }, 40L);

                MessageFormatter formatter = new MessageFormatter()
                                .register("{GUILD}", guild.getName())
                                .register("{TAG}", guild.getTag())
                                .register("{PLAYER}", player.getName());

                if (config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                    PacketSender.sendPacket(player, NotificationUtil.createActionbarNotification(
                                    formatter.format(messages.notificationActionbarEnterGuildRegion)));
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
                    PacketSender.sendPacket(player, NotificationUtil.createTitleNotification(
                                    formatter.format(messages.notificationTitleEnterGuildRegion),
                                    formatter.format(messages.notificationSubtitleEnterGuildRegion),
                                    config.notificationTitleFadeIn, config.notificationTitleStay,
                                    config.notificationTitleFadeOut));
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
                        PacketSender.sendPacket(member, NotificationUtil.createActionbarNotification(
                                        formatter.format(messages.notificationActionbarIntruderEnterGuildRegion)));
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
                        PacketSender.sendPacket(member, NotificationUtil.createTitleNotification(
                                        formatter.format(messages.notificationTitleIntruderEnterGuildRegion),
                                        formatter.format(messages.notificationSubtitleIntruderEnterGuildRegion),
                                        config.notificationTitleFadeIn, config.notificationTitleStay,
                                        config.notificationTitleFadeOut));
                    }
                }

                cache.setNotificationTime(System.currentTimeMillis() + 1000 * config.regionNotificationCooldown);
            }
        });
    }

}
