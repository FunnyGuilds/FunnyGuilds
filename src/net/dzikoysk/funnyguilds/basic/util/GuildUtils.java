package net.dzikoysk.funnyguilds.basic.util;

import java.util.ArrayList;
import java.util.List;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class GuildUtils {
	
	private static List<Guild> guilds = new ArrayList<>();
	
	public static void deleteGuild(final Guild guild){
		if(guild == null) return;
		Bukkit.getScheduler().runTask(FunnyGuilds.getInstance(), new Runnable(){
			@Override
			public void run(){
				Manager.getInstance().stop();
				guild.delete();
				final Region region = RegionUtils.get(guild.getRegion());
				if(region != null){
					if(Settings.getInstance().createStringMaterial.equalsIgnoreCase("ender crystal")){
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
				if(Settings.getInstance().flat) Flat.getGuildFile(guild).delete();
				if(Settings.getInstance().mysql) new DatabaseGuild(guild).delete();
				guild.delete();
				Manager.getInstance().start();
			}
		});
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
		if(guild == null || guild.getName() == null) return;
		if(get(guild.getName()) == null) guilds.add(guild);
	}
	
	public static void removeGuild(Guild guild){
		guilds.remove(guild);
	}
	
}
