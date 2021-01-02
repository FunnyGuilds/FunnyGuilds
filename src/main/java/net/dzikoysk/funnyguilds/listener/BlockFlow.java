package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class BlockFlow implements Listener {

    @EventHandler
    public void lavaFlow(BlockFromToEvent event) {

        if (event.getBlock().getType() != Material.LAVA) {
            return;
        }

        Region region = RegionUtils.getAt(event.getToBlock().getLocation());

        if (region == null) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void waterFlow(BlockFromToEvent event) {

        if (event.getBlock().getType() != Material.WATER) {
            return;
        }

        Region region = RegionUtils.getAt(event.getToBlock().getLocation());

        if (region == null) {
            event.setCancelled(true);
        }

    }

}
