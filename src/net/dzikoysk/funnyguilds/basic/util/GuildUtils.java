package net.dzikoysk.funnyguilds.basic.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.DataManager;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;

public class GuildUtils {
	
	private static List<Guild> guilds = new ArrayList<>();
	private static File guildsFolder = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "guilds" + File.separator);
	
	public static void deleteGuild(Guild guild){
		DataManager.getInstance().stop();
		
		Region region = RegionUtils.get(guild.getRegion());
		if(region != null){
			if(Config.getInstance().createStringMaterial.equalsIgnoreCase("ender crystal")){
				Vector v = region.getCenter().getBlock().getRelative(BlockFace.UP).getLocation().toVector();
				for(Entity e : region.getCenter().getWorld().getEntitiesByClass(EnderCrystal.class))
					if(e.getLocation().getBlock().getLocation().toVector().equals(v)) e.remove();
			} else {
				Block block = region.getCenter().getBlock().getRelative(BlockFace.DOWN);
				if(block.getLocation().getBlockY() > 1) block.setType(Material.AIR);
			}
		}
		IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_GUILD, guild);
		for(String name : guild.getRegions()){
			Region r = RegionUtils.get(name);
			if(r != null) RegionUtils.delete(r);
		}
		UserUtils.removeGuild(guild.getMembers());
		RankManager.getInstance().remove(guild);
		RegionUtils.delete(Region.get(guild.getRegion()));
		for(Guild g : guild.getAllies()) g.removeAlly(guild);
		for(Guild g : guild.getEnemies()) g.removeEnemy(guild);
		if(Config.getInstance().flat) new File(guildsFolder, guild.getName()+".yml").delete();
		if(Config.getInstance().mysql) new DatabaseGuild(guild).delete();
		guild.delete();
		
		DataManager.getInstance().start();
	}
	
	public static Guild get(String name){
		for(Guild guild : guilds){
			if(guild.getName() != null && guild.getName().equalsIgnoreCase(name)) return guild;
		}
		return null;
	}
	
	public static Guild byTag(String tag){
		for(Guild guild : guilds){
			if(guild.getTag() != null && guild.getTag().equalsIgnoreCase(tag.toLowerCase())) return guild;
		}
		return null;
	}
		
	public static boolean isExists(String name){
		for(Guild guild : guilds){
			if(guild.getName() != null && guild.getName().equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	public static boolean tagExists(String tag){
		for(Guild guild : guilds){
			if(guild.getTag() != null && guild.getTag().equalsIgnoreCase(tag)) return true;
		}
		return false;
	}
	
	public static List<String> getNames(List<Guild> lsg){
		List<String> list = new ArrayList<>();
		if(lsg == null) return list;
		for(Guild g : lsg){
			if(g == null) continue;
			if(g.getName() != null) list.add(g.getName());
		}
		return list;
	}
	
	public static List<String> getTags(List<Guild> lsg){
		if(lsg == null) return null;
		List<String> list = new ArrayList<>();
		for(Guild g : lsg)
			if(g.getName() != null) list.add(g.getTag());
		return list;
	}
	
	public static List<Guild> getGuilds(List<String> names){
		if(names == null) return null;
		List<Guild> list = new ArrayList<>();
		for(String s : names){
			if(get(s) != null) list.add(get(s));
		}
		return list;
	}
	
	public static List<Guild> getGuilds(){
		return new ArrayList<Guild>(guilds);
	}
	
	public static void addGuild(Guild guild){
		guilds.add(guild);
	}
	
	public static void removeGuild(Guild guild){
		guilds.remove(guild);
	}
	
}
