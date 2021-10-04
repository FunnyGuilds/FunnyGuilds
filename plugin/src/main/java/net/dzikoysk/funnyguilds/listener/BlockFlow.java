package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;

public class BlockFlow extends AbstractFunnyListener {

    @EventHandler
    public void onFlow(BlockFromToEvent event) {
        if (!event.getBlock().isLiquid()) {
            return;
        }

        Region region = RegionUtils.getAt(event.getToBlock().getLocation());

        if (region == null) {
            event.setCancelled(true);
        }
    }

}
