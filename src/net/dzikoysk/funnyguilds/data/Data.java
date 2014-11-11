package net.dzikoysk.funnyguilds.data;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.configuration.file.YamlConfiguration;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.util.InvitationsList;
import net.dzikoysk.funnyguilds.util.YamlFactor;
import net.dzikoysk.funnyguilds.util.element.PlayerListManager;
import net.dzikoysk.funnyguilds.util.element.PlayerListScheme;

public class Data {
	
	private static enum DO { SAVE, LOAD; }
	private static Data instance;
	private static File folder;
	
	public Data(){
		folder = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "data");
		instance = this;
		invitations(DO.LOAD);
		onlinelist(DO.LOAD);
	}
	
	public void save(){
		invitations(DO.SAVE);
		onlinelist(DO.SAVE);
	}
	
	private void invitations(DO todo){
		File file = new File(folder, "invitations.yml");
		if(todo == DO.SAVE){
			file.delete();
			YamlFactor yaml = new YamlFactor(file);
			for(Entry<String, List<String>> entry : InvitationsList.entrySet())
				yaml.getParent().set(entry.getKey(), entry.getValue());
			yaml.close();
		}else if(todo == DO.LOAD){
			if(!file.exists()) return;
			YamlFactor yf = new YamlFactor(file);
			YamlConfiguration yaml = yf.getParent();
			for(String key : yaml.getKeys(true)){
				String[] check = key.split(",");
				if(check[0].equalsIgnoreCase("U")) InvitationsList.get(User.get(check[0]), Integer.valueOf(check[1])).set(yaml.getStringList(key));
				if(check[0].equalsIgnoreCase("G")){
					Guild guild = GuildUtils.get(check[0]);
					int i = Integer.valueOf(check[1]);
					if(guild != null) InvitationsList.get(guild, i).set(yaml.getStringList(key));
				}
			}
			yf.clear();
		}
		
	}
	
	private void onlinelist(DO todo){
		File file = new File(folder, "playerlist.yml");
		if(todo == DO.SAVE){
			if(!file.exists()){
				YamlFactor yaml = new YamlFactor(file);
				yaml.getParent().set("scheme", PlayerListManager.scheme());
				yaml.close();
			}
		}else if(todo == DO.LOAD){
			if(file.exists()){
				YamlFactor yaml = new YamlFactor(file);
				List<String> scheme = yaml.getParent().getStringList("scheme");
				PlayerListManager.scheme(scheme.toArray(new String[60]));
			}else{
				String[] scheme = PlayerListScheme.uniqueFields();
				PlayerListManager.scheme(scheme);
				YamlFactor yaml = new YamlFactor(file);
				yaml.getParent().set("scheme", scheme);
				yaml.close();
			}
		}
	}
	
	public static Data getInstance(){
		if(instance != null) return instance;
		return new Data();
	}

}
