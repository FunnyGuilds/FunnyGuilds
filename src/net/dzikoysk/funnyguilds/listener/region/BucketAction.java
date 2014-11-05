package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.system.protection.ProtectionUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketAction implements Listener {
	
	@EventHandler
	public void onFill(PlayerBucketFillEvent event){
		if(ProtectionUtils.check(event.getPlayer(), event.getBlockClicked())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onEmpty(PlayerBucketEmptyEvent event){
		if(ProtectionUtils.check(event.getPlayer(), event.getBlockClicked())) event.setCancelled(true);
	}

}
