package net.dzikoysk.funnyguilds.system.protection;

import java.util.concurrent.TimeUnit;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Messages;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class ProtectionUtils {
	
	public static boolean check(Player player, Block block){
		return check(player, block, false);
	}
	
	public static boolean check(Player player, Block block, boolean build){
		if(player == null || block == null) return false;
		Location loc = block.getLocation();
		
		if(!RegionUtils.isIn(loc)) return false;
		Region region = RegionUtils.getAt(loc);
		
		if(region.getGuild() == null) return false;
		User user = User.get(player);
		Guild guild = region.getGuild();
		
		if(guild.getMembers().contains(user)){
			if(build && !guild.canBuild()){
				player.sendMessage(Messages.getInstance().getMessage("regionExplodeInteract").replace("{TIME}",
					Long.toString(TimeUnit.MILLISECONDS.toSeconds(guild.getBuild() - System.currentTimeMillis()))
				));
				return true;
			}
			if(!loc.equals(region.getCenter().getBlock().getRelative(BlockFace.DOWN).getLocation())) return false;
		} else player.sendMessage(Messages.getInstance().getMessage("regionOther"));

		return true;
	}
	
	public static boolean action(Action action, Block block){
		if(action == Action.RIGHT_CLICK_BLOCK) return checkBlock(block);
		return false;
	}
	
	private static boolean checkBlock(Block block){
		switch(block.getType()){
			case CHEST:
			case ENCHANTMENT_TABLE:
			case FURNACE:
			case ENDER_CHEST:
			case WORKBENCH:
			case ANVIL:
				return true;
			default: 
				return false;
		}
	}

}
