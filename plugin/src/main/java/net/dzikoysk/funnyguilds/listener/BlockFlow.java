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

        // Protect guild territory: cancel configured liquids flowing onto a guild-owned region.
        if (this.config.blockFlowOnRegion.contains(block.getType())
                && this.regionManager.findRegionAtLocation(event.getToBlock().getLocation())
                        .filter(region -> region.getGuild() != null)
                        .isPresent()) {
            event.setCancelled(true);
        }
    }

}
