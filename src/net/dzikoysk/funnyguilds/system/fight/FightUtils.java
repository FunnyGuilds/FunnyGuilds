package net.dzikoysk.funnyguilds.system.fight;

import java.util.HashMap;

import net.dzikoysk.funnyguilds.basic.User;

public class FightUtils {
	
	private static HashMap<User, Long> map = new HashMap<>();

	public static void attacked(User victim){
		map.put(victim, System.currentTimeMillis());
	}
	
	public boolean check(User user){
		if(map.containsKey(user))
			if(map.get(user) > System.currentTimeMillis())
				return true;
		return false;
	}
}
