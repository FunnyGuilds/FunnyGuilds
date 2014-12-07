package net.dzikoysk.funnyguilds.data.flat;

import java.io.File;

import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.configuration.PandaConfiguration;

public class FlatUser {
	
	private final User user;
	
	public FlatUser(User user){
		this.user = user;
	}
	
	public boolean serialize(){
		File file = Flat.getUserFile(user);
		if(file.isDirectory()) return false;
		
		PandaConfiguration pc = new PandaConfiguration(file);
		pc.set("uuid", user.getUUID().toString());
		pc.set("name", user.getName());
		pc.set("points", user.getRank().getPoints());
		pc.set("kills", user.getRank().getKills());
		pc.set("deaths", user.getRank().getDeaths());
		pc.set("ban", user.getBan());
		pc.set("reason", user.getReason());
		pc.save();
		pc.clear();
		return true;
	}
	
	public static User deserialize(File file){
		if(file.isDirectory()) return null;
		
		PandaConfiguration pc = new PandaConfiguration(file);
		
		String id = pc.getString("uuid");			
		String name = pc.getString("name");
		int points = pc.getInt("points");
		int kills = pc.getInt("kills");
		int deaths = pc.getInt("deaths");
		long ban = pc.getLong("ban");
		String reason = pc.getString("reason");
		pc.clear();
		
		if(name == null) return null;
		if(id == null) id = new OfflineUser(name).getUniqueId();
		
		Object[] values = new Object[7];
		values[0] = id;
		values[1] = name;
		values[2] = points;
		values[3] = kills;
		values[4] = deaths;
		values[5] = ban;
		values[6] = reason;
		return DeserializationUtils.deserializeUser(values);
	}
}
