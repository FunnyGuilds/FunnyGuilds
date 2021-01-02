package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class BlockFlow implements Listener {

    @EventHandler
    public void blockFlow(BlockFromToEvent event) {

        Block block = event.getBlock();
        if (block.getType() == Material.LAVA || block.getType() == Material.WATER) {
            Region region = RegionUtils.getAt(event.getToBlock().getLocation());

            if (region == null) {
                event.setCancelled(true);
            }
        }

    }

}
