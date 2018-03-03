package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.element.NotificationBar;
import net.dzikoysk.funnyguilds.util.element.notification.NotificationStyle;
import net.dzikoysk.funnyguilds.util.element.notification.NotificationUtil;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.reflect.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        final Player player = event.getPlayer();
        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();

        Bukkit.getScheduler().runTaskAsynchronously(FunnyGuilds.getInstance(), () -> {
            if (from == null || to == null) {
                return;
            }
            
            if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
                return;
            }

            User user = User.get(player);

            Region region = RegionUtils.getAt(to);
            if (region == null && user.getEnter()) {
                user.setEnter(false);
                region = RegionUtils.getAt(from);
                
                if (region != null) {
                    Guild guild = region.getGuild();

                    FunnyGuilds.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(FunnyGuilds.getInstance(), () -> {
                        if (config.createEntityType != null) {
                            EntityUtil.despawn(guild, player);
                        }
                    }, 40L);

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                        PacketSender.sendPacket(player, NotificationUtil.createActionbarNotification(messages.notificationActionbarLeaveGuildRegion
                                        .replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag())));
                    }
                    
                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.BOSSBAR)) {
                        NotificationBar.set(player, messages.notificationBossbarLeaveGuildRegion
                                        .replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()), 1, config.regionNotificationTime);
                    }
                    
                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.CHAT)) {
                        player.sendMessage(messages.notificationChatLeaveGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.TITLE)) {
                        PacketSender.sendPacket(player, NotificationUtil.createTitleNotification(
                                        messages.notificationTitleLeaveGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()),
                                        messages.notificationSubtitleLeaveGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()),
                                        config.notificationTitleFadeIn,
                                        config.notificationTitleStay,
                                        config.notificationTitleFadeOut));
                    }
                }
            } else if (!user.getEnter() && region != null) {
                Guild guild = region.getGuild();
                if (guild == null || guild.getName() == null) {
                    return;
                }
                
                user.setEnter(true);
                FunnyGuilds.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(FunnyGuilds.getInstance(), () -> {
                    if (config.createEntityType != null) {
                        EntityUtil.spawn(guild, player);
                    }
                }, 40L);
                
                if (config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                    PacketSender.sendPacket(player, NotificationUtil.createActionbarNotification(messages.notificationActionbarEnterGuildRegion
                                    .replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag())));
                }
                
                if (config.regionEnterNotificationStyle.contains(NotificationStyle.BOSSBAR)) {
                    NotificationBar.set(player, messages.notificationBossbarEnterGuildRegion
                                    .replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()), 1, config.regionNotificationTime);
                }
                
                if (config.regionEnterNotificationStyle.contains(NotificationStyle.CHAT)) {
                    player.sendMessage(messages.notificationChatEnterGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
                }

                if (config.regionEnterNotificationStyle.contains(NotificationStyle.TITLE)) {
                    PacketSender.sendPacket(player, NotificationUtil.createTitleNotification(
                                    messages.notificationTitleEnterGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()),
                                    messages.notificationSubtitleEnterGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()),
                                    config.notificationTitleFadeIn,
                                    config.notificationTitleStay,
                                    config.notificationTitleFadeOut));
                }

                if (player.hasPermission("funnyguilds.admin.notification")) {
                    return;
                }
                
                if (user.getNotificationTime() > 0 && System.currentTimeMillis() < user.getNotificationTime()) {
                    return;
                }

                if (!config.regionEnterNotificationGuildMember && user.getGuild().equals(guild)) {
                    return;
                }

                for (User u : guild.getOnlineMembers()) {
                    Player member = u.getPlayer();
                    
                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.ACTIONBAR)) {
                        PacketSender.sendPacket(member, NotificationUtil.createActionbarNotification(messages.notificationActionbarIntruderEnterGuildRegion
                                        .replace("{PLAYER}", player.getName())));
                    }
                    
                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.BOSSBAR)) {
                        NotificationBar.set(member, messages.notificationBossbarIntruderEnterGuildRegion
                                        .replace("{PLAYER}", player.getName()), 1, config.regionNotificationTime);
                    }
                    
                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.CHAT)) {
                        member.sendMessage(messages.notificationChatIntruderEnterGuildRegion.replace("{PLAYER}", player.getName()));
                    }

                    if (config.regionEnterNotificationStyle.contains(NotificationStyle.TITLE)) {
                        PacketSender.sendPacket(member, NotificationUtil.createTitleNotification(
                                        messages.notificationTitleIntruderEnterGuildRegion.replace("{PLAYER}", player.getName()),
                                        messages.notificationSubtitleIntruderEnterGuildRegion.replace("{PLAYER}", player.getName()),
                                        config.notificationTitleFadeIn,
                                        config.notificationTitleStay,
                                        config.notificationTitleFadeOut));
                    }
                }
                
                user.setNotificationTime(System.currentTimeMillis() + 1000 * config.regionNotificationCooldown);
            }
        });
    }
}
