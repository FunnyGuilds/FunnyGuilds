package net.dzikoysk.funnyguilds.listener.region;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.SpaceUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplode implements Listener { 
	
	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
        List<Block> destroyed = event.blockList();
        Location loc = event.getLocation();
		
        List<Location> sphere = SpaceUtils.sphere(loc, 3, 3, false, true, 0);
		Random random = new Random();
		for(Location l : sphere){
			Material m = l.getBlock().getType();
			if(m != Material.OBSIDIAN && m != Material.WATER && m != Material.LAVA) continue;
    		if(random.nextFloat() < 0.33f) l.getBlock().breakNaturally();
		}
		
		if(!RegionUtils.isIn(loc)) return;
		Region region = RegionUtils.getAt(loc);
		
		Location protect = region.getCenter().getBlock().getRelative(BlockFace.DOWN).getLocation();
		
        Iterator<Block> it = destroyed.iterator();
        while(it.hasNext()) {
        	if(it.next().getLocation().equals(protect)) it.remove();
        }
        
        Guild guild = region.getGuild();
        guild.setBuild(System.currentTimeMillis() + Settings.getInstance().regionExplode*1000);
        for(User user : guild.getMembers()){
        	Player player = Bukkit.getPlayer(user.getName());
        	if(player != null) player.sendMessage(Messages.getInstance().getMessage("regionExplode")
        		.replace("{TIME}", Integer.toString(Settings.getInstance().regionExplode)));
        }
    }
	
}