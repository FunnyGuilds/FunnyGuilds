package net.dzikoysk.funnyguilds.system.security;

import java.util.ArrayList;
import java.util.List;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SecuritySystem {

	private static SecuritySystem instance;
	private List<User> blocked;
	
	public SecuritySystem(){
		instance = this;
		blocked = new ArrayList<>();
	}

	public boolean checkPlayer(Player player, Object... values){
		for(SecurityType type : SecurityType.values())
			if(checkPlayer(player, type, values)) return true;
		return false;
	}
	
	public boolean checkPlayer(Player player, SecurityType type, Object... values){
		if(isBanned(User.get(player))) return true;
		switch(type){
		case FREECAM:
			Guild guild = null;
			for(int i = 0; i < values.length; i++) if(values[i] instanceof Guild) guild = (Guild) values[i];
			int dis = (int) RegionUtils.get(guild.getRegion()).getCenter().distance(player.getLocation());
			if(dis < 6) return false;
			for(Player w : Bukkit.getOnlinePlayers()) if(w.isOp()){
				w.sendMessage(SecurityUtils.getBustedMessage(player.getName(), "FreeCam"));
				w.sendMessage(SecurityUtils.getNoteMessage("Zaatakowal krysztal z odleglosci &c" + dis + " kratek"));
			}
			blocked.add(User.get(player));
			return true;
		case EVERYTHING:
			break;
		default:
			break;
		}
		return false;
	}
	
	public boolean isBanned(User user){
		return blocked.contains(user);
	}
	
	public static SecuritySystem getSecurity(){
		if(instance == null) new SecuritySystem();
		return instance;
	}
}
