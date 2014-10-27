package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.element.NotificationBar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		
		Location loc = event.getTo();
		Player player = event.getPlayer();
		User user = User.get(player);
		
		if(RegionUtils.isIn(loc)){
			if(user.getEnter()) return;
			
			Messages m = Messages.getInstance();

			Guild guild = RegionUtils.getAt(loc).getGuild();
			if(guild == null || guild.getName() == null) return;
			
			if(guild.getMembers().contains(user)){
				player.sendMessage(m.getMessage("regionEnter")
					.replace("{GUILD}", guild.getName())
					.replace("{TAG}", guild.getTag()));
				user.setEnter(true);
				return;
			}
			
			player.sendMessage(m.getMessage("notificationOther")
				.replace("{GUILD}", guild.getName())
				.replace("{TAG}", guild.getTag())
			);
			
			user.setEnter(true);
			
			if(user.getNotificationTime() > 0 && System.currentTimeMillis() < user.getNotificationTime()) return;
			
			NotificationBar.set(player, m.getMessage("notificationOther")
				.replace("{GUILD}", guild.getName())
				.replace("{TAG}", guild.getTag())
			, 1, Config.getInstance().regionNotificationTime);
			
			for(User u : guild.getMembers()){
				if(u.getName() == null) continue;
				Player member = Bukkit.getPlayer(u.getName());
				if(member == null) continue;
				NotificationBar.set(member, m.getMessage("notificationMember")
					.replace("{PLAYER}", player.getName())
				, 1, Config.getInstance().regionNotificationTime);
			}
			
			user.setNotificationTime(System.currentTimeMillis() + 1000 * Config.getInstance().regionNotificationCooldown);
			return;
		}
		if(user.getEnter()){
			if(RegionUtils.getAt(event.getFrom()) != null){
				Guild guild = RegionUtils.getAt(event.getFrom()).getGuild();
				player.sendMessage(Messages.getInstance().getMessage("regionLeave")
					.replace("{GUILD}", guild.getName())
					.replace("{TAG}", guild.getTag())
				);
			} user.setEnter(false);
		}
	}
}
