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

import java.util.List;

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
                        if ("ender crystal".equalsIgnoreCase(config.createStringMaterial)) {
                            EntityUtil.despawn(guild, player);
                        }
                    }, 40L);

                    if (guild.getMembers().contains(user)) {
                        player.sendMessage(messages.regionLeave.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
                        return;
                    }

                    player.sendMessage(messages.regionLeave.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));

                    if (config.regionEnterNotificationStyle == NotificationStyle.TITLE) {
                        List<Object> titlePacket = NotificationUtil.createTitleNotification(
                                messages.notificationTitleLeavedGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()),
                                messages.notificationSubtitleLeavedGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()),
                                config.notificationTitleFadeIn,
                                config.notificationTitleStay,
                                config.notificationTitleFadeOut
                        );
                        PacketSender.sendPacket(player, titlePacket);
                    }
                    else {
                        NotificationBar.set(player, messages.regionLeave.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()), 1, config.regionNotificationTime);
                    }
                }
            } else if (!user.getEnter() && region != null) {
                Guild guild = region.getGuild();
                if (guild == null || guild.getName() == null) {
                    return;
                }
                
                user.setEnter(true);
                FunnyGuilds.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(FunnyGuilds.getInstance(), () -> {
                    if ("ender crystal".equalsIgnoreCase(config.createStringMaterial)) {
                        EntityUtil.spawn(guild, player);
                    }
                }, 40L);
                
                if (guild.getMembers().contains(user)) {
                    player.sendMessage(messages.regionEnter.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
                    return;
                }

                player.sendMessage(messages.notificationEnterGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));

                if (config.regionEnterNotificationStyle == NotificationStyle.TITLE) {
                    List<Object> titlePacket = NotificationUtil.createTitleNotification(
                            messages.notificationTitleEnterOnGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()),
                            messages.notificationSubtitleEnterOnGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()),
                            config.notificationTitleFadeIn,
                            config.notificationTitleStay,
                            config.notificationTitleFadeOut
                    );
                    PacketSender.sendPacket(player, titlePacket);
                }
                else {
                    NotificationBar.set(player, messages.notificationEnterGuildRegion.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()), 1, config.regionNotificationTime);
                }

                if (player.hasPermission("funnyguilds.admin.notification")) {
                    return;
                }
                
                if (user.getNotificationTime() > 0 && System.currentTimeMillis() < user.getNotificationTime()) {
                    return;
                }

                for (User u : guild.getOnlineMembers()) {
                    if (u.getName() == null) {
                        continue;
                    }
                    
                    Player member = u.getPlayer();
                    
                    member.sendMessage(messages.notificationIntruderEnterGuildRegion.replace("{PLAYER}", player.getName()));

                    if (config.regionEnterNotificationStyle == NotificationStyle.TITLE) {
                        List<Object> titlePacket = NotificationUtil.createTitleNotification(
                                messages.notificationTitleIntruderEnteredOnGuildRegion.replace("{PLAYER}", player.getName()),
                                messages.notificationSubtitleIntruderEnteredOnGuildRegion.replace("{PLAYER}", player.getName()),
                                config.notificationTitleFadeIn,
                                config.notificationTitleStay,
                                config.notificationTitleFadeOut
                        );
                        PacketSender.sendPacket(member, titlePacket);
                    }
                    else {
                        NotificationBar.set(member, messages.notificationIntruderEnterGuildRegion.replace("{PLAYER}", player.getName()), 1, config.regionNotificationTime);
                    }
                }
                
                user.setNotificationTime(System.currentTimeMillis() + 1000 * config.regionNotificationCooldown);
            }
        });
    }
}
