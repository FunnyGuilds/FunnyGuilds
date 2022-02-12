package net.dzikoysk.funnyguilds.listener;

import java.time.Instant;
import java.time.LocalTime;
import net.dzikoysk.funnyguilds.guild.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TntProtection extends AbstractFunnyListener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        if (!config.tntProtection.time.enabled && !config.tntProtection.time.enabledGlobal) {
            return;
        }

        LocalTime now = LocalTime.now();
        LocalTime start = config.tntProtection.time.startTime.getTime();
        LocalTime end = config.tntProtection.time.endTime.getTime();

        boolean isWithinTimeframe = config.tntProtection.time.passingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        if (!isWithinTimeframe) {
            return;
        }

        if (config.tntProtection.time.enabledGlobal) {
            event.setCancelled(true);
            return;
        }

        if (config.tntProtection.time.enabled) {
            if (this.regionManager.findRegionAtLocation(event.getLocation()).isPresent()) {
                event.setCancelled(true);
                return;
            }

            event.blockList().removeIf(block -> this.regionManager.findRegionAtLocation(block.getLocation()).isPresent());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBuildingOnGuildRegionOnExplosion(EntityExplodeEvent event) {
        this.regionManager.findRegionAtLocation(event.getLocation())
                .flatMap(Region::getGuildOption)
                .peek(guild -> guild.setBuild(Instant.now().plusSeconds(config.regionExplode).toEpochMilli()));
    }

}
