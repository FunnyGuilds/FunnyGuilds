package net.dzikoysk.funnyguilds.data;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class Messages {
	
	private static Messages instance;
	private HashMap<String, String> single = new HashMap<>();
	private HashMap<String, List<String>> multiple = new HashMap<>();
	
	public Messages(){
		instance = this;

		File file = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "messages.yml");
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
		
		for(String key : yml.getKeys(true)){
			if(key.toLowerCase().contains("list")){
				List<String> list =  yml.getStringList(key);
				for(int i = 0; i < list.size(); i++){
					list.set(i, ChatColor.translateAlternateColorCodes('&', list.get(i))
						.replace("Ä", "")
						.replace("Â", "")
					);
				}
				multiple.put(key, list);
				continue;
			}
			String message = ChatColor.translateAlternateColorCodes('&', yml.getString(key));
			single.put(key, message
				.replace("Ä", "")
				.replace("Â", "")
			);
		}
	}
	
	public String getMessage(String key){
		return single.get(key);
	}
	
	public List<String> getList(String key){
		return multiple.get(key);
	}
	
	public static Messages getInstance(){
		if(instance != null) return instance;
		return new Messages();
	}

}
