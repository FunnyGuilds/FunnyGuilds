package net.dzikoysk.funnyguilds.system.event.christmas;

import java.util.Collections;
import java.util.List;

public class ChristmasStats {

	private static ChristmasStats instance;
	
	public ChristmasStats(){
		instance = this;
	}
	
	public ChristmasStats sort(){
		Collections.sort(ChristmasUser.getUsers());
		return this;
	}
	
	public int getPosition(ChristmasUser user){
		 return ChristmasUser.getUsers().indexOf(user) + 1;
	}
	
	public ChristmasUser getChristmasUser(int i){
		if(ChristmasUser.getUsers().size() > i - 1) return ChristmasUser.getUsers().get(i - 1);
		return null;
	}
	
	public List<ChristmasUser> getGiftStats(){
		return ChristmasUser.getUsers();
	}
	
	public static ChristmasStats getInstance(){
		if(instance == null) new ChristmasStats();
		return instance.sort();
	}
}
