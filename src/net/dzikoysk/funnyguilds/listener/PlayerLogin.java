package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.system.ban.BanUtils;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerLogin implements Listener {
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event){
		if(Bukkit.hasWhitelist()) return;
		if(!UserUtils.playedBefore(event.getPlayer().getName())) return;
		User user = User.get(event.getPlayer());
		if(!user.isBanned()) return;
		if(!BanUtils.check(user)) return;
		event.disallow(Result.KICK_BANNED, BanUtils.getBanMessage(user));
	}
	
}
