package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.system.protection.ProtectionUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class BlockIgnite implements Listener {
	
	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		if(ProtectionUtils.check(event.getPlayer(), event.getBlock())) event.setCancelled(true);
	}
	
}

