package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.listener.region.util.ProtectionUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

public class BlockDamage implements Listener {
	
	@EventHandler
	public void onDamage(BlockDamageEvent event) {
		if(ProtectionUtils.check(event.getPlayer(), event.getBlock())) event.setCancelled(true);
	}
	
}

