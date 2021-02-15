package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.time.LocalTime;
import java.util.Iterator;

public class TntProtection implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (!config.guildTNTProtectionEnabled && !config.guildTNTProtectionGlobal) {
            return;
        }

        LocalTime now = LocalTime.now();
        LocalTime start = config.guildTNTProtectionStartTime;
        LocalTime end = config.guildTNTProtectionEndTime;

        boolean isWithinTimeframe = config.guildTNTProtectionPassingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        if (!isWithinTimeframe) {
            return;
        }

        if (config.guildTNTProtectionGlobal) {
            event.setCancelled(true);
        }
        else if (config.guildTNTProtectionEnabled) {
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

}
