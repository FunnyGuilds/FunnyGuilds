package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.system.ban.BanUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class PlayerLogin implements Listener {
	
	@EventHandler
	public void onLogin(AsyncPlayerPreLoginEvent event){
		User user = User.get(event.getName());
		if(!user.isBanned()) return;
		if(!BanUtils.check(user)) return;
		event.disallow(Result.KICK_BANNED, BanUtils.getBanMessage(user));
	}

}
