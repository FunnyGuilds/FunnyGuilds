package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamage implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof EnderCrystal){
			if(ProtectionSystem.endercrystal(event)) event.setCancelled(true);
			return;
		}
		if(event.getEntity() instanceof Player){
			Player attacker = null;
			if(event.getDamager() instanceof Player) attacker = (Player) event.getDamager();
			else if(event.getDamager() instanceof Projectile){
				LivingEntity le = ((Projectile) event.getDamager()).getShooter();
				if(le instanceof Player) attacker = (Player) le;
			}
			if(attacker == null) return;
			Player victim = (Player) event.getEntity();
			User uv = User.get(victim);
			User ua = User.get(attacker);
			if(!(uv.hasGuild() && ua.hasGuild())) return;
			if(uv.getGuild().getName().equals(ua.getGuild().getName()))
				if(!Config.getInstance().damageGuild) event.setCancelled(true);
			if(uv.getGuild().getAllies().contains(ua.getGuild()))
				if(!Config.getInstance().damageAlly) event.setCancelled(true);
		}
	}
}
