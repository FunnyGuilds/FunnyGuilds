package net.dzikoysk.funnyguilds.data;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.configuration.PandaConfiguration;

import org.bukkit.ChatColor;

public class Messages {
	
	private static Messages instance;
	private static String version = "2.5 Valor";
	private static File messages =  new File("plugins/FunnyGuilds", "messages.yml");
	
	private HashMap<String, String> single = new HashMap<>();
	private HashMap<String, List<String>> multiple = new HashMap<>();
	
	public Messages(){
		instance = this;
		PandaConfiguration pc = loadConfiguration();
		if(pc == null){
			FunnyGuilds.error("[Messages] Messages.yml not loaded!");
			return;
		}
		for(String key : pc.getKeys()){
			if(key.toLowerCase().contains("list")){
				List<String> list =  pc.getStringList(key);
				if(list == null) continue;
				for(int i = 0; i < list.size(); i++){
					list.set(i, ChatColor.translateAlternateColorCodes('&', list.get(i))
						.replace("Ä", "")
						.replace("Â", "")
					);
				}
				multiple.put(key, list);
				continue;
			}
			String message = ChatColor.translateAlternateColorCodes('&', pc.getString(key));
			single.put(key, message
				.replace("Ä", "")
				.replace("Â", "")
			);
		}
	}
	
	private PandaConfiguration loadConfiguration(){
		PandaConfiguration pc = new PandaConfiguration(messages);
		String version = pc.getString("version");
		if(version != null && version.equals(Messages.version)) return pc;
		FunnyGuilds.info("Updating the plugin messages ...");
		Map<String, Object> values = pc.getMap();
		messages.delete();
		DataManager.loadDefaultFiles(new String[] { "messages.yml" });
		pc = new PandaConfiguration(messages);
		for(Entry<String, Object> entry : values.entrySet())
			pc.set(entry.getKey(), entry.getValue());
		pc.set("version", Messages.version);
		pc.save(messages);
		FunnyGuilds.info("Successfully updated messages!");
		return pc;
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
