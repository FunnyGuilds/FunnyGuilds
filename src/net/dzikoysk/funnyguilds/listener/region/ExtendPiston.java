package net.dzikoysk.funnyguilds.listener.region;

import java.util.List;

import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.util.SpaceUtils;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class ExtendPiston implements Listener {
	
	@EventHandler
	public void onExtend(BlockPistonExtendEvent event){
		int i = 3 + event.getLength();
		List<Location> sphere = SpaceUtils.sphere(event.getBlock().getLocation(), i, i, false, true, 0);
		for(Location l : sphere)
			if(ProtectionSystem.center(l)) event.setCancelled(true);
	}

}
