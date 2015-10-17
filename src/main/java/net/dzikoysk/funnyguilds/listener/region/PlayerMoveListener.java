package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.element.NotificationBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        final Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(FunnyGuilds.getInstance(), new Runnable() {

            @Override
            public void run() {
                if (from == null || to == null)
                    return;
                if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())
                    return;

                Messages m = Messages.getInstance();
                User user = User.get(player);

                Region region = RegionUtils.getAt(to);
                if (region == null && user.getEnter()) {
                    user.setEnter(false);
                    region = RegionUtils.getAt(from);
                    if (region != null) {
                        Guild guild = region.getGuild();
                        player.sendMessage(m.getMessage("regionLeave")
                                        .replace("{GUILD}", guild.getName())
                                        .replace("{TAG}", guild.getTag())
                        );
                    }
                } else if (!user.getEnter() && region != null) {
                    Guild guild = region.getGuild();
                    if (guild == null || guild.getName() == null)
                        return;
                    user.setEnter(true);

                    if (guild.getMembers().contains(user)) {
                        player.sendMessage(m.getMessage("regionEnter")
                                .replace("{GUILD}", guild.getName())
                                .replace("{TAG}", guild.getTag()));
                        return;
                    }

                    player.sendMessage(m.getMessage("notificationOther")
                                    .replace("{GUILD}", guild.getName())
                                    .replace("{TAG}", guild.getTag())
                    );

                    if (player.hasPermission("funnyguilds.admin.notification"))
                        return;
                    if (user.getNotificationTime() > 0 && System.currentTimeMillis() < user.getNotificationTime())
                        return;

                    NotificationBar.set(player, m.getMessage("notificationOther")
                            .replace("{GUILD}", guild.getName())
                            .replace("{TAG}", guild.getTag())
                            , 1, Settings.getInstance().regionNotificationTime);

                    for (User u : guild.getMembers()) {
                        if (u.getName() == null)
                            continue;
                        Player member = Bukkit.getPlayer(u.getName());
                        if (member == null)
                            continue;
                        NotificationBar.set(member, m.getMessage("notificationMember")
                                .replace("{PLAYER}", player.getName())
                                , 1, Settings.getInstance().regionNotificationTime);
                    }
                    user.setNotificationTime(System.currentTimeMillis() + 1000 * Settings.getInstance().regionNotificationCooldown);
                }
            }
        });
    }
}
