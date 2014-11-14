package net.dzikoysk.funnyguilds.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class Messages {
	
	private static Messages instance;
	private static String version = "2.2 Valor";
	private static File messages =  new File("plugins/FunnyGuilds", "messages.yml");
	
	private HashMap<String, String> single = new HashMap<>();
	private HashMap<String, List<String>> multiple = new HashMap<>();
	
	public Messages(){
		instance = this;
		
		YamlConfiguration yml = loadConfiguration();
		if(yml == null){
			FunnyGuilds.error("[Messages] Messages.yml not loaded!");
			return;
		}
		
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
	
	private YamlConfiguration loadConfiguration(){
		YamlConfiguration yml =  YamlConfiguration.loadConfiguration(messages);
		String version = yml.getString("version");
		if(version != null && version.equals(Messages.version)) return yml;
		FunnyGuilds.info("Updating the plugin messages ...");
		Map<String, Object> values = yml.getValues(true);
		messages.delete();
		DataManager.loadDefaultFiles(new String[] { "messages.yml" });
		yml = YamlConfiguration.loadConfiguration(messages);
		for(Entry<String, Object> entry : values.entrySet()) yml.set(entry.getKey(), entry.getValue());
		try {
			yml.save(messages);
			FunnyGuilds.info("Successfully updated messages!");
		} catch (IOException e){
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
		return yml;
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
