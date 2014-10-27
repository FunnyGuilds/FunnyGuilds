package net.dzikoysk.funnyguilds.basic.util;

import java.util.ArrayList;
import java.util.Collections;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Rank;
import net.dzikoysk.funnyguilds.basic.User;

public class RankManager {
	
	private static RankManager instance;
	private final ArrayList<Rank> users = new ArrayList<>();
	private final ArrayList<Rank> guilds = new ArrayList<>();
	
	public RankManager(){
		instance = this;
	}
	
	public void update(User user){
		if(!users.contains(user.getRank())) users.add(user.getRank());
		Collections.sort(users);
	}
	
	public void update(Guild guild){
		if(!guilds.contains(guild.getRank())) guilds.add(guild.getRank());
		Collections.sort(guilds);
	}
	
	public void remove(User user){
		users.remove(user.getRank());
		Collections.sort(users);
	}
	
	public void remove(Guild guild){
		guilds.remove(guild.getRank());
		Collections.sort(guilds);
	}
	
	public int getPosition(User user){
		return users.indexOf(user.getRank())+1;
	}
	
	public int getPosition(Guild guild){
		return guilds.indexOf(guild.getRank())+1;
	}
	
	public User getUser(int i){
		if(i-1 < users.size()) return users.get(i-1).getUser();
		return null;
	}
	
	public Guild getGuild(int i){
		if(i-1 < guilds.size()) return guilds.get(i-1).getGuild();
		return null;
	}
	
	public int users(){
		return users.size();
	}
	
	public int guilds(){
		return guilds.size();
	}
	
	public static RankManager getInstance(){
		if(instance == null) new RankManager();
		return instance;
	}

}
