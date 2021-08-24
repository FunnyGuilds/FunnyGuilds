package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.PluginHook;
import net.dzikoysk.funnyguilds.shared.bukkit.EntityUtils;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import panda.std.Option;

public class EntityDamage implements Listener {

    private final FunnyGuilds funnyGuilds;

    public EntityDamage(FunnyGuilds funnyGuilds) {
        this.funnyGuilds = funnyGuilds;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        UserManager userManager = funnyGuilds.getUserManager();
        EntityUtils.getAttacker(event.getDamager()).peek(attacker -> {
            Option<User> attackerUserOp = userManager.getUser(attacker);

            if (attackerUserOp.isEmpty()) {
                return;
            }

            User attackerUser = attackerUserOp.get();
            PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
            Entity victim = event.getEntity();

            if (config.animalsProtection && (victim instanceof Animals || victim instanceof Villager)) {
                RegionUtils.getAtOpt(victim.getLocation())
                        .map(Region::getGuild)
                        .filterNot(guild -> guild.equals(attackerUser.getGuild()))
                        .peek(guild -> event.setCancelled(true));

                return;
            }

            if (!(victim instanceof Player)) {
                return;
            }

            Option<User> victimOp = userManager.getUser(victim.getUniqueId());

            if (victimOp.isEmpty()) {
                return;
            }

            User victimUser = victimOp.get();

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

                    if (! (attackerUser.getGuild().getPvP(victimUser.getGuild()) && victimUser.getGuild().getPvP(attackerUser.getGuild()))) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            if (attacker.equals(victim)) {
                return;
            }

            if (!config.assistEnable || event.isCancelled()) {
                return;
            }

            if (PluginHook.isPresent(PluginHook.PLUGIN_WORLDGUARD) && PluginHook.WORLD_GUARD.isInIgnoredRegion(victim.getLocation())) {
                return;
            }

            victimUser.getCache().addDamage(attackerUser, event.getDamage(), System.currentTimeMillis());
        });
    }
    
}
