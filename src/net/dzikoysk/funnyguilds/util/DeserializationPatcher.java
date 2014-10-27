package net.dzikoysk.funnyguilds.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.data.Config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class DeserializationPatcher {
	
	public static Object[] deserializeGuild(YamlConfiguration yaml){
		Object[] values = new Object[10];
		String id = yaml.getString("uuid");
		String name = yaml.getString("name");
		String tag = yaml.getString("tag");
		String os = yaml.getString("owner");
		String hs = yaml.getString("home");
		String region = yaml.getString("region");
		List<String> ms = yaml.getStringList("members");
		List<String> rgs = yaml.getStringList("regions");
		List<String> als = yaml.getStringList("allies");
		List<String> ens = yaml.getStringList("enemies");
		
		if(name == null){
			FunnyGuilds.error("[Deserialize] Cannot deserialize guild! Caused by: name is null");
			return null;
		}else if(tag == null){
			FunnyGuilds.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: tag is null");
			return null;
		}else if(os == null){
			FunnyGuilds.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: owner is null");
			return null;
		}else if(region == null){
			FunnyGuilds.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region is null");
			return null;
		}
		
		UUID uuid = UUID.randomUUID();
		if(id != null) uuid = UUID.fromString(id);
		
		User owner = User.get(os);
		
		Region rg = RegionUtils.get(region);
		if(rg == null){
			FunnyGuilds.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region (object) is null");
			return null;
		}
		
		Location home = rg.getCenter();
		if(hs != null) home = Parser.parseLocation(hs);
		
		if(ms == null || ms.isEmpty()){
			ms = new ArrayList<String>();
			ms.add(os);
		}
		List<User> members = UserUtils.getUsers(ms);
		
		List<String> regions = new ArrayList<>();
		if(rgs != null)
			for(String n : rgs) if(RegionUtils.get(n) != null) regions.add(n);
		
		List<Guild> allies = new ArrayList<>();
		if(als != null)
			for(String s : als) allies.add(Guild.get(s));

		List<Guild> enemies = new ArrayList<Guild>();
		if(ens != null)
			for(String s : ens) enemies.add(Guild.get(s));

		values[0] = uuid;
		values[1] = name;
		values[2] = tag;
		values[3] = owner;
		values[4] = home;
		values[5] = region;
		values[6] = members;
		values[7] = regions;
		values[8] = allies;
		values[9] = enemies;
		return values;
	}
	
	public static Object[] deserializeRegion(YamlConfiguration yaml){
		Object[] values = new Object[4];
		String name = yaml.getString("name");
		String cs = yaml.getString("center");
		int size = yaml.getInt("size");
		int enlarge = yaml.getInt("enlarge");
		
		if(name == null || cs == null){
			FunnyGuilds.error("Cannot deserialize region! Caused by: name/center is null");
			return null;
		}
		
		String[] ca = (cs).split(",");
		World world = Bukkit.getWorld(ca[0]);
		if(world == null) world = Bukkit.getWorlds().get(0);
		Location center = new Location(world, Integer.valueOf(ca[1]), Integer.valueOf(ca[2]), Integer.valueOf(ca[3]));
		
		if(size < 1) size = Config.getInstance().regionSize;
			
		values[0] = name;
		values[1] = center;
		values[2] = size;
		values[3] = enlarge;
		return values;
	}

}
