package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.material.MaterialData;

public class BlockPhysics implements Listener {

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        MaterialData md = Settings.getConfig().createMaterialData;
        if (md == null || !event.getBlock().getState().getData().equals(md)) {
            return;
        }

        Location loc = event.getBlock().getLocation();
        Region region = RegionUtils.getAt(loc);

        if (region == null) {
            return;
        }

        if (loc.equals(region.getCenter().getBlock().getRelative(BlockFace.DOWN).getLocation())) {
            event.setCancelled(true);
        }
    }
    
}
