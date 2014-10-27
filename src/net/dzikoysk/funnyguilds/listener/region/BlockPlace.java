package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.listener.region.util.ProtectionUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace implements Listener {
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if(ProtectionUtils.check(event.getPlayer(), event.getBlock(), true)) event.setCancelled(true);
	}
	
}
