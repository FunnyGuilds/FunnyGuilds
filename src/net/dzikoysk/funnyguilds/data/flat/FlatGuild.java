package net.dzikoysk.funnyguilds.data.flat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.DataManager;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.YamlFactor;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

public class FlatGuild {
	
	private final Guild guild;
	
	public FlatGuild(Guild guild){
		this.guild = guild;
	}
	
	public boolean serialize() {
		if(guild.getName() == null){
			FunnyGuilds.error("[Serialize] Cannot serialize guild! Caused by: name is null");
			return false;
		}else if(guild.getTag() == null){
			FunnyGuilds.error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: tag is null");
			return false;
		}else if(guild.getOwner() == null){
			FunnyGuilds.error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: owner is null");
			return false;
		}else if(guild.getRegion() == null){
			FunnyGuilds.error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: region is null");
			return false;
		}else if(guild.getUUID() == null) guild.setUUID(UUID.randomUUID());
		
		File file = DataManager.loadCustomFile(BasicType.GUILD, guild.getName());
		YamlFactor yml = new YamlFactor(file);
		YamlConfiguration yaml = yml.getParent();
		
		yaml.set("uuid", guild.getUUID().toString());
		yaml.set("name", guild.getName());
		yaml.set("tag", guild.getTag());
		yaml.set("owner", guild.getOwner().getName());
		yaml.set("home", Parser.toString(guild.getHome()));
		yaml.set("members", UserUtils.getNames(guild.getMembers()));
		yaml.set("region", guild.getRegion());
		yaml.set("regions", guild.getRegions());
		yaml.set("allies", GuildUtils.getNames(guild.getAllies()));
		yaml.set("enemies", GuildUtils.getNames(guild.getEnemies()));
		yaml.set("born", guild.getBorn());
		yaml.set("validity", guild.getBorn());
		yaml.set("attacked", guild.getAttacked());
		yaml.set("lives", guild.getLives());
		yaml.set("ban", guild.getBan());
		
		yml.close();
		return true;
	}
	
	public static Guild deserialize(File file){
		YamlFactor yml = new YamlFactor(file);
		YamlConfiguration yaml = yml.getParent();
		Object[] values = new Object[15];
		
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
		long born = yaml.getLong("born");
		long validity = yaml.getLong("validity");
		long attacked = yaml.getLong("attacked");
		long ban = yaml.getLong("ban");
		int lives = yaml.getInt("lives");
		
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
		
		if(born == 0) born = System.currentTimeMillis(); 
		if(validity == 0) validity = System.currentTimeMillis() + Config.getInstance().validityStart; 
		if(lives == 0) lives = Config.getInstance().warLives; 
		
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
		values[10] = born;
		values[11] = validity;
		values[12] = attacked;
		values[13] = lives;
		values[14] = ban;
		return DeserializationUtils.deserializeGuild(values);
	}

}
