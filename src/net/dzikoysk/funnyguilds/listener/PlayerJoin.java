package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.util.Version;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		User user = User.get(player);
		user.getScoreboard();
		
		IndependentThread.actions(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, player);
		IndependentThread.actions(ActionType.RANK_UPDATE_USER, user);
		IndependentThread.action(ActionType.PLAYERLIST_SEND, user);
		
		Version.check(player);
	}
}
