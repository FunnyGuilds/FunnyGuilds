package net.dzikoysk.funnyguilds.data;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.util.InvitationsList;
import net.dzikoysk.funnyguilds.util.configuration.PandaConfiguration;
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
		playerlist(DO.LOAD);
	}
	
	public void save(){
		invitations(DO.SAVE);
		playerlist(DO.SAVE);
	}
	
	private void invitations(DO todo){
		File file = new File(folder, "invitations.yml");
		if(todo == DO.SAVE){
			file.delete();
			PandaConfiguration pc = new PandaConfiguration(file);
			for(Entry<String, List<String>> entry : InvitationsList.entrySet())
				pc.set(entry.getKey(), entry.getValue());
			pc.save();
			pc.clear();
		} else if(todo == DO.LOAD){
			if(!file.exists()) return;
			PandaConfiguration pc = new PandaConfiguration(file);
			for(String key : pc.getKeys()){
				String[] check = key.split(",");
				if(check[0].equalsIgnoreCase("U")) 
					InvitationsList.get(User.get(check[0]), Integer.valueOf(check[1])).set(pc.getStringList(key));
				if(check[0].equalsIgnoreCase("G")){
					Guild guild = GuildUtils.get(check[0]);
					int i = Integer.valueOf(check[1]);
					if(guild != null) InvitationsList.get(guild, i).set(pc.getStringList(key));
				}
			}
			pc.clear();
		}
		
	}
	
	private void playerlist(DO todo){
		File file = new File(folder, "playerlist.yml");
		if(todo == DO.SAVE){
			if(!file.exists()){
				PandaConfiguration pc = new PandaConfiguration(file);
				pc.set("scheme", PlayerListManager.scheme());
				pc.save();
				pc.clear();
			}
		}else if(todo == DO.LOAD){
			if(file.exists()){
				PandaConfiguration pc = new PandaConfiguration(file);
				List<String> scheme = pc.getStringList("scheme");
				if(scheme == null || scheme.isEmpty()){
					String[] sh = PlayerListScheme.uniqueFields();
					PlayerListManager.scheme(sh);
					pc = new PandaConfiguration(file);
					pc.set("scheme", scheme);
					pc.save();
					pc.clear();
					return;
				}
				PlayerListManager.scheme(scheme.toArray(new String[60]));
				pc.clear();
			} else {
				String[] scheme = PlayerListScheme.uniqueFields();
				PlayerListManager.scheme(scheme);
				PandaConfiguration pc = new PandaConfiguration(file);
				pc.set("scheme", scheme);
				pc.save();
				pc.clear();
			}
		}
	}
	
	public static File getPlayerListFile(){
		return new File(folder, "playerlist.yml");
	}
	
	public static Data getInstance(){
		if(instance != null) return instance;
		return new Data();
	}

}
