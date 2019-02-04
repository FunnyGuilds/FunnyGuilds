package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class BlockPhysics implements Listener {

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        Pair<Material, Byte> md = FunnyGuilds.getInstance().getPluginConfiguration().createMaterial;
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
