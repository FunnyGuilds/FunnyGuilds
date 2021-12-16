package net.dzikoysk.funnyguilds.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;

public class BlockFlow extends AbstractFunnyListener {

    @EventHandler
    public void onFlow(BlockFromToEvent event) {
        if (!event.getBlock().isLiquid()) {
            return;
        }

        if (!this.regionManager.isInRegion(event.getToBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

}
