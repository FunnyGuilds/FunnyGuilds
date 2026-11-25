package net.dzikoysk.funnyguilds.listener.region;

import java.util.Objects;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildPreRegionTransitionEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionEnterEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionLeaveEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.inject.annotations.Inject;

public class PlayerMove extends AbstractFunnyListener {

    @Inject
    public GuildEntityHelper guildEntityHelper;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        this.checkTransition(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        this.checkTransition(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMoveAccepted(PlayerMoveEvent event) {
        this.notifyTransition(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleportAccepted(PlayerTeleportEvent event) {
        this.notifyTransition(event);
    }

    private void checkTransition(PlayerMoveEvent event) {
        Transition transition = this.findTransition(event);
        if (transition == null) {
            return;
        }

        if (!SimpleEventHandler.handle(new GuildPreRegionTransitionEvent(transition.user(), transition.from(), transition.to()))) {
            event.setCancelled(true);
        }
    }

    private void notifyTransition(PlayerMoveEvent event) {
        // Another Bukkit listener may have cancelled the movement or changed its destination.
        Transition transition = this.findTransition(event);
        if (transition == null) {
            return;
        }

        Player player = event.getPlayer();
        Guild from = transition.from();
        Guild to = transition.to();
        if (from != null) {
            this.logger.debug("Player " + player.getName() + " left region of guild " + from.getName());
            this.messageService.getMessage(config -> config.notificationLeaveGuildRegion)
                    .with(new FunnyFormatter().register("{GUILD}", from.getName()).register("{TAG}", from.getTag()))
                    .receiver(player)
                    .send();
        }

        BukkitScheduler scheduler = this.plugin.getServer().getScheduler();
        if (to != null) {
            this.notifyEntry(player, transition.user(), to);
            if (this.config.heart.createEntityType != null) {
                scheduler.runTaskLater(this.plugin, () -> {
                    if (player.isOnline() && to.equals(this.findGuild(player.getLocation()))) {
                        this.guildEntityHelper.spawnGuildEntity(to, player);
                    }
                }, 40L);
            }
        }

        scheduler.runTaskAsynchronously(this.plugin, () -> {
            if (from != null) {
                Bukkit.getPluginManager().callEvent(new GuildRegionLeaveEvent(EventCause.USER, transition.user(), from));
            }
            if (to != null) {
                Bukkit.getPluginManager().callEvent(new GuildRegionEnterEvent(EventCause.USER, transition.user(), to));
            }
        });
    }

    private void notifyEntry(Player player, User user, Guild guild) {
        this.logger.debug("Player " + player.getName() + " entered region of guild " + guild.getName());
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", player.getName());

        this.messageService.getMessage(config -> config.notificationEnterGuildRegion)
                .with(formatter)
                .receiver(player)
                .send();

        UserCache cache = user.getCache();
        long now = System.currentTimeMillis();
        if (player.hasPermission("funnyguilds.admin.notification") || now < cache.getNotificationTime()
                || (!this.config.regionEnterNotificationGuildMember && user.getGuild().filter(guild::equals).isPresent())) {
            return;
        }

        this.messageService.getMessage(config -> config.notificationIntruderEnterGuildRegion)
                .with(formatter)
                .receiver(guild)
                .send();
        cache.setNotificationTime(now + 1000L * this.config.regionNotificationCooldown);
    }

    private @Nullable Transition findTransition(PlayerMoveEvent event) {
        Location to = event.getTo();
        if (event.isCancelled() || to == null || !event.hasChangedBlock()) {
            return null;
        }

        Guild fromGuild = this.findGuild(event.getFrom());
        Guild toGuild = this.findGuild(to);
        if (Objects.equals(fromGuild, toGuild)) {
            return null;
        }

        return this.userManager.findByPlayer(event.getPlayer())
                .map(user -> new Transition(user, fromGuild, toGuild))
                .orElse(null);
    }

    private @Nullable Guild findGuild(Location location) {
        return this.regionManager.findRegionAtLocation(location).map(Region::getGuild).orElse(null);
    }

    private record Transition(User user, @Nullable Guild from, @Nullable Guild to) { }

}
