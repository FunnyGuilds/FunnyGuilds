package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Config;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamage implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) { 
			Player victim = (Player) event.getEntity();
			Player attacker = (Player) event.getDamager();
			User uv = User.get(victim);
			User ua = User.get(attacker);
			if(!(uv.hasGuild() && ua.hasGuild())) return;
			if(uv.getGuild().getName().equals(ua.getGuild().getName())){
				if(!Config.getInstance().damageGuild) event.setCancelled(true);
			}
			if(uv.getGuild().getAllies().contains(ua.getGuild())){
				if(!Config.getInstance().damageAlly) event.setCancelled(true);
			}
		}
	}
}
