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

        if (!this.regionManager.isInRegion(block.getLocation())) {
            event.setCancelled(true);
        }
    }

}
