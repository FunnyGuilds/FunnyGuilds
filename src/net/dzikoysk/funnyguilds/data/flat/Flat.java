package net.dzikoysk.funnyguilds.data.flat;

import java.io.File;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;

public class Flat {
	
	private static Flat instance;
	
	private static File guildsFolder = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "guilds");
	private static File regionsFolder = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "regions");
	private static File usersFolder = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "data" + File.separator + "users" + File.separator);
	
	public Flat(){
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
		for(User user : UserUtils.getUsers())
			if(user.getUUID() != null && user.getName() != null) new FlatUser(user).serialize();
	}
	
	private void loadUsers(){	
		int i = 0;
		File[] path = usersFolder.listFiles();
		if(path != null) for(File file : path)
			if(FlatUser.deserialize(file) == null) file.delete();
		if(i > 0) FunnyGuilds.warning("Repaired conflicts: " + i);
		FunnyGuilds.info("Loaded users: " + UserUtils.getUsers().size());	
	}
	
	private void saveRegions(){
		int i = 0;
		for(Region region : RegionUtils.getRegions()){
			if(!new FlatRegion(region).serialize()){
				RegionUtils.delete(region);
				i++;
			}
		}
		if(i > 0) FunnyGuilds.warning("Deleted defective regions: " + i);
	}
	
	private void loadRegions(){
		File[] path = regionsFolder.listFiles();
		if(path != null) for(File file : path) {
			if(FlatRegion.deserialize(file) == null) file.delete();
		}
		FunnyGuilds.info("Loaded regions: " + RegionUtils.getRegions().size());
	}
	
	private void saveGuilds(){
		int i = 0;
		for(Guild guild : GuildUtils.getGuilds()){
			if(!new FlatGuild(guild).serialize()){
				GuildUtils.deleteGuild(guild);
				i++;
			}
		}
		if(i > 0) FunnyGuilds.warning("Deleted defective guild: " + i);
	}
	
	private void loadGuilds(){
		GuildUtils.getGuilds().clear();
		File[] path = guildsFolder.listFiles();
		if(path != null) for(File file : path){
			if(FlatGuild.deserialize(file) == null) file.delete();
		}
		IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE);
		FunnyGuilds.info("Loaded guilds: " + GuildUtils.getGuilds().size());
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
	
	public static File getUserFile(User user){
		return new File(usersFolder, user.getName() + ".yml");
	}
	
	public static Flat getInstance(){
		if(instance != null) return instance;
		return new Flat();
	}

}
