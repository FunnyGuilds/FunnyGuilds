package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (!(entity instanceof Player)) {
            return;
        }

        Player attacker = null;
        if (damager instanceof Player) {
            attacker = (Player) damager;
        } else if (damager instanceof Projectile) {
            LivingEntity le = (LivingEntity) ((Projectile) damager).getShooter();
            if (le instanceof Player) {
                attacker = (Player) le;
            }
        }

        if (attacker == null) {
            return;
        }

        PluginConfig config = Settings.getConfig();
        User victimUser = User.get((Player) event.getEntity());
        User attackerUser = User.get(attacker);

        if (victimUser.hasGuild() && attackerUser.hasGuild()) {
            if (victimUser.getUUID().equals(attackerUser.getUUID())) {
                return;
            }
            
            if (victimUser.getGuild().equals(attackerUser.getGuild())) {
                if (!victimUser.getGuild().getPvP()) {
                    event.setCancelled(true);
                    return;
                }
            }
            
            if (victimUser.getGuild().getAllies().contains(attackerUser.getGuild())) {
                if (!config.damageAlly) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        
        if (config.assistEnable) {
            victimUser.addDamage(attackerUser, event.getDamage());
        }
    }
    
}
