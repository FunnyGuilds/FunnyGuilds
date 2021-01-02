package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.guild.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;

public class BlockFlow implements Listener {

    @EventHandler
    public void blockFlow(BlockFromToEvent event) {

        if (!event.getBlock().isLiquid()) {
            return;
        }

        Region region = RegionUtils.getAt(event.getToBlock().getLocation());

        if (region == null) {
            event.setCancelled(true);
        }

    }

}
