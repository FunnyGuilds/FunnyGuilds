package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamage implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event){
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		if(entity instanceof Player){
			Player attacker = null;
			if(damager instanceof Player) attacker = (Player) damager;
			else if(damager instanceof Projectile){
				LivingEntity le = ((Projectile) damager).getShooter();
				if(le instanceof Player) attacker = (Player) le;
			}
			if(attacker == null) return;
			Player victim = (Player) event.getEntity();
			User uv = User.get(victim);
			User ua = User.get(attacker);
			if(!(uv.hasGuild() && ua.hasGuild())) return;
			if(uv.getGuild().equals(ua.getGuild()))
				if(!uv.getGuild().getPvP()) event.setCancelled(true);
			if(uv.getGuild().getAllies().contains(ua.getGuild()))
				if(!Settings.getInstance().damageAlly) event.setCancelled(true);
		} else if(entity instanceof EnderCrystal){
			if(damager instanceof Player){
				if(ProtectionSystem.endercrystal((EnderCrystal) entity, (Player) damager))
					event.setCancelled(true);
			} else if(damager instanceof Projectile){
				if(ProtectionSystem.endercrystal((EnderCrystal) entity) != null)
					event.setCancelled(true);
			}
		}
	}
}
