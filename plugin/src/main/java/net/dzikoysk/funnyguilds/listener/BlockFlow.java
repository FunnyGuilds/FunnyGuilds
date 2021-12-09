package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.guild.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;
import panda.std.Option;

public class BlockFlow extends AbstractFunnyListener {

    @EventHandler
    public void onFlow(BlockFromToEvent event) {
        if (!event.getBlock().isLiquid()) {
            return;
        }

        Option<Region> regionOption = this.regionManager.findRegionAtLocation(event.getToBlock().getLocation());
        if (regionOption.isEmpty()) {
            event.setCancelled(true);
        }
    }

}
