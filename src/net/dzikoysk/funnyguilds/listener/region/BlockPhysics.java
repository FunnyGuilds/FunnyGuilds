package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Config;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class BlockPhysics implements Listener { 

	@EventHandler
	public void onPhysics(BlockPhysicsEvent event) {
		Material m = Config.getInstance().createMaterial;
		if(m == null || event.getBlock().getType() != m) return;
		
		Location loc = event.getBlock().getLocation();
		
		if(!RegionUtils.isIn(loc)) return;
		Region region = RegionUtils.getAt(loc);
		
		if(loc.equals(region.getCenter().getBlock().getRelative(BlockFace.DOWN).getLocation())){
			event.setCancelled(true);
		}
	}
}
