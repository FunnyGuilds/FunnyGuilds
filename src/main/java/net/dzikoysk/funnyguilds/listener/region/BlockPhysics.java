package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class BlockPhysics implements Listener {

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        Pair<Material, Byte> md = Settings.getConfig().createMaterial;
        if (md == null || event.getBlock().getType() != md.getLeft()) {
            return;
        }

        Location loc = event.getBlock().getLocation();
        Region region = RegionUtils.getAt(loc);

        if (region == null) {
            return;
        }

        if (loc.equals(region.getHeart())) {
            event.setCancelled(true);
        }
    }
    
}
