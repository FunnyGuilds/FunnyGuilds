package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.Version;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.reflect.PacketExtension;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		final Player player = e.getPlayer();
		User user = User.get(player);
		user.getScoreboard();
		
		IndependentThread.actions(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, player);
		IndependentThread.actions(ActionType.DUMMY_GLOBAL_UPDATE_USER, user);
		IndependentThread.actions(ActionType.RANK_UPDATE_USER, user);
		IndependentThread.action(ActionType.PLAYERLIST_SEND, user);
		
		Bukkit.getScheduler().runTaskLaterAsynchronously(FunnyGuilds.getInstance(), new Runnable(){
			@Override
			public void run(){
				PacketExtension.registerPlayer(player);
				if(Settings.getInstance().createMaterial == null) EntityUtil.spawn(player);
				Version.check(player);
			}
		}, 40L);
	}
}
