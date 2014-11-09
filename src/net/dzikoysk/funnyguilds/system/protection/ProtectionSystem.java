package net.dzikoysk.funnyguilds.system.protection;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.system.war.WarSystem;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ProtectionSystem {
	
	public static boolean endercrystal(EntityDamageByEntityEvent event){
		EnderCrystal ec = (EnderCrystal) event.getEntity();
		if(!RegionUtils.isIn(ec.getLocation())) return false;
		Region region = RegionUtils.getAt(ec.getLocation());
		if(region == null) return false;
		if(region.getCenter().getBlock().getRelative(BlockFace.UP).getLocation().toVector()
			.equals(ec.getLocation().getBlock().getLocation().toVector())){
			if(event.getDamager() instanceof Player){
				WarSystem.getInstance().attack((Player) event.getDamager(), region.getGuild());
			}
			return true;
		}
		return false;
	}

}
