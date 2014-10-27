package net.dzikoysk.funnyguilds.basic.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;

import org.bukkit.Location;

public class RegionUtils {
	
	public static List<Region> regions = new ArrayList<>();
	private static File regionsFolder = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "regions" + File.separator);
	
	public static Region get(String name){
		for(Region region : regions){
			if(region.getName().equalsIgnoreCase(name)) return region;
		}
		return null;
	}
	
	public static boolean isIn(Location loc){
		for(Region region : regions){
			if(region.isIn(loc)) return true;
		}
		return false;
	}
	
	public static Region getAt(Location loc){
		for(Region region : regions){
			if(region.isIn(loc)) return region;
		}
		return null;
	}
	
	public static boolean isNear(Location center){
		for(Region region : regions){
			double distance = center.distance(region.getCenter());
			int i = Config.getInstance().regionSize;
			if(Config.getInstance().enlargeItems != null) i = Config.getInstance().enlargeItems.size()*Config.getInstance().enlargeSize + i;
			if(distance < (2 * i + Config.getInstance().regionMinDistance)){
				return true;
			}
		}
		return false;	
	}
	
	public static void delete(Region region){
		if(Config.getInstance().flat){
			File file = new File(regionsFolder, region.getName()+".yml");
			file.delete();
		}
		if(Config.getInstance().mysql){
			new DatabaseRegion(region).delete();
		}
		region.delete();
	}
	
	public static List<Region> getRegions(){
		List<Region> x = new ArrayList<Region>(regions);
		return x;
	}
	
	public static List<String> getNames(List<Region> lsg){
		List<String> list = new ArrayList<>();
		if(lsg == null) return list;
		for(Region r : lsg){
			if(r == null) continue;
			if(r.getName() != null) list.add(r.getName());
		}
		return list;
	}
	
	public static void addRegion(Region region){
		regions.add(region);
	}
	
	public static void removeRegion(Region region){
		regions.remove(region);
	}
}
