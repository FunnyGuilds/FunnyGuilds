package net.dzikoysk.funnyguilds.system.event.christmas;

import java.io.File;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.util.configuration.PandaConfiguration;

public class ChristmasData {

	private static final File CHRISTMAS_USER_FILE = new File("plugins/FunnyGuilds/data/event/christmas/christmas_users.pc");
	
	private static ChristmasData instance;
	
	public ChristmasData(){
		instance = this;
		loadFile();
	}

	public void load(){
		loadFile();
		PandaConfiguration pc = new PandaConfiguration(CHRISTMAS_USER_FILE);
		for(String key : pc.getKeys()){
			String value = pc.getString(key);
			if(value == null || value.isEmpty()) continue;
			String[] values = value.split("!");
			if(value == null || value.isEmpty()) continue;
			ChristmasUser user = ChristmasUser.get(User.get(key));
			user.setGifts(Integer.valueOf(values[0]));
			user.setRedGifts(Integer.valueOf(values[1]));
			user.setGreenGifts(Integer.valueOf(values[2]));
			user.setEmeraldGifts(Integer.valueOf(values[3]));
			user.setBypass(Boolean.valueOf(values[4]));
			user.setSeeMap(Boolean.valueOf(values[5]));
		}
	}
	
	public void save(){
		loadFile();
		PandaConfiguration pc = new PandaConfiguration(CHRISTMAS_USER_FILE);
		for(ChristmasUser user : ChristmasUser.getUsers()){
			pc.set(
				user.getName(), 
				user.getGifts() + "!" +
				user.getRedGifts() + "!" +
				user.getGreenGifts() + "!" +
				user.getEmeraldGifts() + "!" +
				user.hasBypass() + "!" +
				user.seeMap()
			);
		}
		pc.save();
	}
	
	private void loadFile(){
		if(!CHRISTMAS_USER_FILE.exists()){
			try { 
				CHRISTMAS_USER_FILE.getParentFile().mkdirs();
				CHRISTMAS_USER_FILE.createNewFile(); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ChristmasData getInstance(){
		return instance;
	}
}
