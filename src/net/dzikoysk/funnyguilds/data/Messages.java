package net.dzikoysk.funnyguilds.data;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.configuration.Yamler;

import org.bukkit.ChatColor;

public class Messages {
	
	private static Messages instance;
	private static String version = "3.0 Christmas";
	private static File messages =  new File(FunnyGuilds.getInstance().getDataFolder(), "messages.yml");
	
	private HashMap<String, String> single = new HashMap<>();
	private HashMap<String, List<String>> multiple = new HashMap<>();
	
	public Messages(){
		instance = this;
		Manager.loadDefaultFiles(new String[] { "messages.yml" });
		Yamler pc = loadConfiguration();
		if(pc == null){
			FunnyGuilds.error("[Messages] Messages.yml not loaded!");
			return;
		}
		for(String key : pc.getKeys(true)){
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
	
	private Yamler loadConfiguration(){
		Yamler pc = new Yamler(messages);
		String version = pc.getString("version");
		if(version != null && version.equals(Messages.version)) return pc;
		FunnyGuilds.info("Updating the plugin messages ...");
		messages.renameTo(new File(FunnyGuilds.getInstance().getDataFolder(), "messages.old"));
		Manager.loadDefaultFiles(new String[] { "messages.yml" });
		pc = new Yamler(messages);
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
