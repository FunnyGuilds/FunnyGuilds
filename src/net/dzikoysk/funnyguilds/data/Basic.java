package net.dzikoysk.funnyguilds.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.DeserializationPatcher;
import net.dzikoysk.funnyguilds.util.IndependentThread;
import net.dzikoysk.funnyguilds.util.YamlFactor;

public class Basic {
	
	private static Basic instance;
	
	private static File guildsFolder = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "guilds");
	private static File regionsFolder = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "regions");
	private static File usersFolder = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "data" + File.separator + "users" + File.separator);
	
	public Basic(){
		instance = this;
		loadUsers();
		loadRegions();
		loadGuilds();
		checkObjects();
	}
	
	public void save() {
		saveUsers();
		saveRegions();
		saveGuilds();
	}
	
	private void saveUsers(){
		if(UserUtils.getUsers() == null || UserUtils.getUsers().isEmpty()) return;
		for(User u : UserUtils.getUsers()){
			if(u.getUUID() != null && u.getName() != null){
				YamlFactor yml = new YamlFactor(new File(usersFolder, u.getName()+".yml"));
				yml.getParent().set("uuid", u.getUUID().toString());
				yml.getParent().set("name", u.getName());
				yml.getParent().set("points", u.getRank().getPoints());
				yml.getParent().set("kills", u.getRank().getKills());
				yml.getParent().set("deaths", u.getRank().getDeaths());
				yml.close();
			}
		}
	}
	
	private void loadUsers(){	
		int i = 0;
		File[] path = usersFolder.listFiles();
		if(path != null) for(File file : path) {
			YamlFactor yaml = new YamlFactor(file);
			
			String id = yaml.getParent().getString("uuid");			
			String name = yaml.getParent().getString("name");
			int points = yaml.getParent().getInt("points");
			int kills = yaml.getParent().getInt("kills");
			int deaths = yaml.getParent().getInt("deaths");
			
			if(id == null){
				file.delete();
				i++;
				continue;
			}
			
			UUID uuid = UUID.fromString(id);
			User u = User.get(uuid);
			u.setName(name);
			u.getRank().setKills(kills);
			u.getRank().setDeaths(deaths);
			u.getRank().setPoints(points);
		}
		if(i > 0) FunnyGuilds.warning("Repaired conflicts: " + i);
		FunnyGuilds.info("Loaded users: " + UserUtils.getUsers().size());	
	}
	
	private void saveRegions(){
		int i = 0;
		for(Region region : RegionUtils.getRegions()){
			try {
				File file = DataManager.loadCustomFile(BasicType.REGION, region.getName());
				YamlFactor yaml = new YamlFactor(file);
				Object o = region.serialize(yaml.getParent());
				if(o != null) yaml.close();
				else{
					RegionUtils.delete(region);
					file.delete();
					i++;
				}
			} catch (Exception e) {
				FunnyGuilds.error("An error occurred while saving the region! Caused by: Exception");
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}
		if(i > 0) FunnyGuilds.warning("Deleted defective guild: " + i);
	}
	
	private void loadRegions(){
		File[] path = regionsFolder.listFiles();
		if(path != null) for(File file : path) {
			try {
				YamlFactor yaml = new YamlFactor(file);
				Object[] values = DeserializationPatcher.deserializeRegion(yaml.getParent());
				Region.deserialize(values);
			} catch (Exception e) {
				FunnyGuilds.error("An error occurred while loading the region! Caused by: Exception");
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}
		FunnyGuilds.info("Loaded regions: " + RegionUtils.getRegions().size());
	}
	
	private void saveGuilds(){
		int i = 0;
		for(Guild guild : GuildUtils.getGuilds()){
			try {
				File file = DataManager.loadCustomFile(BasicType.GUILD, guild.getName());
				YamlFactor yaml = new YamlFactor(file);
				Object o = guild.serialize(yaml.getParent());
				if(o != null) yaml.close();
				else {
					GuildUtils.deleteGuild(guild);
					file.delete();
					i++;
				}
			} catch (Exception e) {
				FunnyGuilds.error("An error occurred while saving the guild! Caused by: Exception");
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}
		if(i > 0) FunnyGuilds.warning("Deleted defective guild: " + i);
	}
	
	private void loadGuilds(){
		File[] path = guildsFolder.listFiles();
		if(path != null) for(File file : path){
			try {
				YamlFactor yaml = new YamlFactor(file);
				Object[] values = DeserializationPatcher.deserializeGuild(yaml.getParent());
				Guild.deserialize(values);
			} catch (Exception e){
				FunnyGuilds.error("An error occurred while loading the guild! Caused by: Exception");
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}
		IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE);
		FunnyGuilds.info("Loaded guilds: " + GuildUtils.getGuilds().size());
	}
	
	public void checkFiles(){
		File[] rfiles = regionsFolder.listFiles();
		File[] gfiles = guildsFolder.listFiles();
		List<File> regions = new ArrayList<>();
		List<File> guilds = new ArrayList<>();
		if(rfiles != null) regions = Arrays.asList(regionsFolder.listFiles());
		if(gfiles != null) guilds = Arrays.asList(guildsFolder.listFiles());
		int i = 0;
		for(File region : regions){
			boolean exists = false;
			for(File guild : guilds){
				if(region.getName().equalsIgnoreCase(guild.getName())){
					exists = true;
					break;
				}
			}
			if(exists) continue;
			region.delete();
			i++;
		}
		for(File guild : guilds){
			boolean exists = false;
			for(File region : regions){
				if(guild.getName().equalsIgnoreCase(region.getName())){
					exists = true;
					break;
				}
			}
			if(exists) continue;
			guild.delete();
			i++;
		}
		if(i > 0) FunnyGuilds.warning("Repaired conflicts: " + i);
	}
	
	public void checkObjects(){
		List<String> guilds = GuildUtils.getNames(GuildUtils.getGuilds());
		List<String> regions = RegionUtils.getNames(RegionUtils.getRegions());
		int i = 0;
		for(Guild guild : GuildUtils.getGuilds()){
			if(guild.getName() != null && regions.contains(guild.getName())){
				guilds.remove(guild.getName());
				continue;
			}
			GuildUtils.deleteGuild(guild);
			i++;
		}
		
		guilds = GuildUtils.getNames(GuildUtils.getGuilds());
		regions = RegionUtils.getNames(RegionUtils.getRegions());
		
		for(Region region : RegionUtils.getRegions()){
			if(region.getName() != null && guilds.contains(region.getName())){
				regions.remove(region.getName());
				continue;
			}
			RegionUtils.delete(region);
			i++;
		}
		if(i > 0) FunnyGuilds.warning("Repaired conflicts: " + i);
	}
	
	public static Basic getInstance(){
		if(instance != null) return instance;
		return new Basic();
	}
}
