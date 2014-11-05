package net.dzikoysk.funnyguilds.system.ban;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;

public class BanManager {

	private static BanManager instance;
	
	public BanManager(){
		instance = this;
	}
	
	public void run(){
		for(Guild guild : GuildUtils.getGuilds()){
			guild.isBanned();
		}
	}
	
	public static BanManager getInstance(){
		if(instance == null) new BanManager();
		return instance;
	}
}
