package net.dzikoysk.funnyguilds.data.flat;

import java.io.File;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.YamlFactor;

public class FlatUser {
	
	private final User user;
	
	public FlatUser(User user){
		this.user = user;
	}
	
	public boolean serialize(){
		YamlFactor yaml = new YamlFactor(Flat.getUserFile(user));
		yaml.getParent().set("uuid", user.getUUID().toString());
		yaml.getParent().set("name", user.getName());
		yaml.getParent().set("points", user.getRank().getPoints());
		yaml.getParent().set("kills", user.getRank().getKills());
		yaml.getParent().set("deaths", user.getRank().getDeaths());
		yaml.getParent().set("ban", user.getBan());
		yaml.getParent().set("reason", user.getReason());
		yaml.close();
		return true;
	}
	
	public static User deserialize(File file){
		YamlFactor yaml = new YamlFactor(file);
		Object[] values = new Object[7];
		
		String id = yaml.getParent().getString("uuid");			
		String name = yaml.getParent().getString("name");
		int points = yaml.getParent().getInt("points");
		int kills = yaml.getParent().getInt("kills");
		int deaths = yaml.getParent().getInt("deaths");
		long ban = yaml.getParent().getLong("ban");
		String reason = yaml.getParent().getString("reason");
		yaml.close();
		
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
