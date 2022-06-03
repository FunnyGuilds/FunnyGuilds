package net.dzikoysk.funnyguilds.listener;

import java.time.Instant;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.EntityUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import panda.std.Option;

public class EntityDamage extends AbstractFunnyListener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        EntityUtils.getAttacker(event.getDamager()).peek(attacker -> {
            Option<User> attackerUserOption = this.userManager.findByPlayer(attacker);
            if (attackerUserOption.isEmpty()) {
                return;
            }

            User attackerUser = attackerUserOption.get();
            Entity victim = event.getEntity();

            if (this.config.animalsProtection && (victim instanceof Animals || victim instanceof Villager)) {
                this.regionManager.findRegionAtLocation(victim.getLocation())
                        .map(Region::getGuild)
                        .filterNot(guild -> attackerUser.getGuild()
                                .map(it -> it.equals(guild))
                                .orElseGet(false))
                        .peek(guild -> event.setCancelled(true));

                return;
            }

            Option<User> victimOption = Option.of(victim)
                    .is(Player.class)
                    .flatMap(this.userManager::findByPlayer);

            if (victimOption.isEmpty()) {
                return;
            }

            User victimUser = victimOption.get();
            if (victimUser.hasGuild() && attackerUser.hasGuild()) {
                if (victimUser.getUUID().equals(attackerUser.getUUID())) {
                    return;
                }

                Guild victimGuild = victimUser.getGuild().get();
                Guild attackerGuild = attackerUser.getGuild().get();

                if (victimGuild.equals(attackerGuild)) {
                    if (!victimGuild.hasPvPEnabled()) {
                        event.setCancelled(true);
                        return;
                    }
                }

                if (victimGuild.isAlly(attackerGuild)) {
                    if (!this.config.damageAlly) {
                        event.setCancelled(true);
                        return;
                    }

                    if (!(attackerGuild.hasAllyPvPEnabled(victimGuild) && victimGuild.hasAllyPvPEnabled(attackerGuild))) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            if (attacker.equals(victim)) {
                return;
            }

            if (!this.config.assistEnable || event.isCancelled()) {
                return;
            }

            if (HookManager.WORLD_GUARD.map(worldGuard -> worldGuard.isInIgnoredRegion(victim.getLocation()))
                    .orElseGet(false)) {
                return;
            }

            victimUser.getCache().addDamage(attackerUser, event.getDamage(), Instant.now());
        });
    }

}
