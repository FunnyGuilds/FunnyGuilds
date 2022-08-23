package net.dzikoysk.funnyguilds.listener;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.Cooldown;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.TimeDivision;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TntProtection extends AbstractFunnyListener {

    private final Cooldown<UUID> informationMessageCooldowns = new Cooldown<>();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        if (!this.config.tntProtection.time.enabled && !this.config.tntProtection.time.enabledGlobal) {
            return;
        }

        LocalTime now = LocalTime.now();
        LocalTime start = this.config.tntProtection.time.startTime.getTime();
        LocalTime end = this.config.tntProtection.time.endTime.getTime();

        boolean isWithinTimeframe = this.config.tntProtection.time.passingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        if (!isWithinTimeframe) {
            return;
        }

        if (this.config.tntProtection.time.enabledGlobal) {
            event.setCancelled(true);
            return;
        }

        if (this.config.tntProtection.time.enabled) {
            if (this.regionManager.findRegionAtLocation(event.getLocation()).isPresent()) {
                event.setCancelled(true);
                return;
            }

            event.blockList().removeIf(block -> this.regionManager.findRegionAtLocation(block.getLocation()).isPresent());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBuildingOnGuildRegionOnExplosion(EntityExplodeEvent event) {
        String message = FunnyFormatter.format(this.messages.regionExplode, "{TIME}", TimeUtils.formatTime(this.config.regionExplode, TimeDivision.Form.ACCUSATIVE));

        this.regionManager.findRegionAtLocation(event.getLocation())
                .map(Region::getGuild)
                .filterNot(guild -> this.config.regionExplodeExcludeEntities.contains(event.getEntityType()))
                .peek(guild -> guild.setBuild(Instant.now().plus(this.config.regionExplode)))
                .toStream(guild -> guild.getMembers().stream())
                .filterNot(user -> this.informationMessageCooldowns.cooldown(user.getUUID(), this.config.infoPlayerCooldown))
                .forEach(user -> user.sendMessage(message));
    }

}
