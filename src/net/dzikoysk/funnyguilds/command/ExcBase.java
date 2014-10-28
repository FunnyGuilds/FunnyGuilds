package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ExcBase implements Executor {
	
	public void execute(CommandSender s, String[] args){
	    
	    final Messages m = Messages.getInstance();
		final Player p = (Player)s;
		final User user = User.get(p);
		
		if(!user.hasGuild()){
			p.sendMessage(m.getMessage("baseHasNotGuild"));
			return;
		}
		
		final Region region = Region.get(user.getGuild().getRegion());
		
		if(region == null){
			p.sendMessage(m.getMessage("baseHasNotRegion"));
			return;
		}
		
		if(region.getCenter() == null){
			p.sendMessage(m.getMessage("baseHasNotCenter"));
			return;
		}
		
		if(user.getTeleportation() != null){
			p.sendMessage(m.getMessage("baseIsTeleportation"));
			return;
		}
		
		final Vector before = p.getLocation().toVector();
		final int time = Config.getInstance().baseDelay;
		if(time < 1){
			p.teleport(region.getCenter());
			p.sendMessage(m.getMessage("baseTeleport"));
			return;
		}
		
		p.sendMessage(m.getMessage("baseDontMove")
			.replace("{TIME}", time+"")
		);
		
		user.setTeleportation(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), new Runnable(){		
			int i = 0;
			public void run(){
				i++;
				if(!p.getLocation().toVector().equals(before)){
					user.getTeleportation().cancel();
					p.sendMessage(m.getMessage("baseMove"));
					user.setTeleportation(null);
					return;
				}
				if(i > time){
					user.getTeleportation().cancel();
					p.sendMessage(m.getMessage("baseTeleport"));
					p.teleport(region.getCenter());
					user.setTeleportation(null);
					return;
				}
			}
		}, 0, 20));
		return;
	}
}
