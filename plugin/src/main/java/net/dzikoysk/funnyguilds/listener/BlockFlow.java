package net.dzikoysk.funnyguilds.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;

public class BlockFlow extends AbstractFunnyListener {

    @EventHandler
    public void onFlow(BlockFromToEvent event) {
        Block block = event.getBlock();
        if (!block.isLiquid()) {
            return;
        }

        // Confine liquids to regions: cancel flow originating outside any region.
        if (this.config.blockFlow && !this.regionManager.isInRegion(block.getLocation())) {
            event.setCancelled(true);
            return;
        }

        // Protect guild territory: cancel liquid flowing onto a region.
        if (this.config.blockFlowOnRegion && this.regionManager.isInRegion(event.getToBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

}
