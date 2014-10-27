package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.listener.region.util.ProtectionUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract implements Listener {
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(ProtectionUtils.action(event.getAction(), event.getClickedBlock())) return;
		if(ProtectionUtils.check(event.getPlayer(), event.getClickedBlock(), true)) event.setCancelled(true);
	}
}
