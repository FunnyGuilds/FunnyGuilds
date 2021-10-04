package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Iterator;

public class TntProtection extends AbstractFunnyListener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        if (!config.tntProtection.time.enabled && !config.tntProtection.time.enabledGlobal) {
            return;
        }

        LocalTime now = LocalTime.now();
        LocalTime start = config.tntProtection.time.startTime;
        LocalTime end = config.tntProtection.time.endTime;

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
            Region region = RegionUtils.getAt(event.getLocation());

            if (region != null) {
                event.setCancelled(true);
                return;
            }

            Iterator<Block> affectedBlocks = event.blockList().iterator();

            while (affectedBlocks.hasNext()) {
                Block block = affectedBlocks.next();
                Region maybeRegion = RegionUtils.getAt(block.getLocation());

                if (maybeRegion != null) {
                    affectedBlocks.remove();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBuildingOnGuildRegionOnExplosion(EntityExplodeEvent event) {
        Location explosionLocation = event.getLocation();
        Region region = RegionUtils.getAt(explosionLocation);

        if (region != null) {
            region.getGuild().setBuild(Instant.now().plusSeconds(config.regionExplode).toEpochMilli());
        }
    }

}
