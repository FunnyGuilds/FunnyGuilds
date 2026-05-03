package net.dzikoysk.funnyguilds.listener;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.util.UUID;
import net.dzikoysk.funnyguilds.damage.DamageManager;
import net.dzikoysk.funnyguilds.damage.DamageState;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuardHook.FriendlyFireStatus;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.shared.RankFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.EntityUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;

public class EntityDamage extends AbstractFunnyListener {

    @Inject
    private DamageManager damageManager;
    @Inject
    private RankSystem rankSystem;

    private final Cache<PredictionKey, Boolean> predictionCooldowns = Caffeine.newBuilder()
            .expireAfter(new Expiry<PredictionKey, Boolean>() {
                @Override
                public long expireAfterCreate(PredictionKey key, Boolean value, long currentTime) {
                    return Math.max(0L, EntityDamage.this.config.combatPredictionInterval.toNanos());
                }

                @Override
                public long expireAfterUpdate(PredictionKey key, Boolean value, long currentTime, long currentDuration) {
                    return currentDuration;
                }

                @Override
                public long expireAfterRead(PredictionKey key, Boolean value, long currentTime, long currentDuration) {
                    return currentDuration;
                }
            })
            .build();

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

                boolean shouldReturn = HookManager.WORLD_GUARD
                        .map(hook -> {
                            FriendlyFireStatus victimFriendlyFire = hook.getFriendlyFireStatus(victim.getLocation());
                            FriendlyFireStatus attackerFriendlyFire = hook.getFriendlyFireStatus(attacker.getLocation());

                            if (victimFriendlyFire == FriendlyFireStatus.ALLOW && attackerFriendlyFire == FriendlyFireStatus.ALLOW) {
                                return FriendlyFireStatus.ALLOW;
                            } else if (victimFriendlyFire == FriendlyFireStatus.DENY || attackerFriendlyFire == FriendlyFireStatus.DENY) {
                                return FriendlyFireStatus.DENY;
                            }

                            return FriendlyFireStatus.INHERIT;
                        })
                        .orElse(FriendlyFireStatus.INHERIT)
                        .map(friendlyFire -> {
                            if (friendlyFire == FriendlyFireStatus.ALLOW) {
                                return false;
                            }

                            if (victimGuild.equals(attackerGuild) && (!victimGuild.hasPvPEnabled() || friendlyFire == FriendlyFireStatus.DENY)) {
                                event.setCancelled(true);
                                return true;
                            }

                            if (victimGuild.isAlly(attackerGuild)) {
                                if (friendlyFire == FriendlyFireStatus.DENY) {
                                    event.setCancelled(true);
                                    return true;
                                }

                                if (!this.config.damageAlly) {
                                    event.setCancelled(true);
                                    return true;
                                }

                                if (!(attackerGuild.hasAllyPvPEnabled(victimGuild) && victimGuild.hasAllyPvPEnabled(attackerGuild))) {
                                    event.setCancelled(true);
                                    return true;
                                }
                            }

                            return false;
                        })
                        .get();

                if (shouldReturn) {
                    return;
                }
            }

            if (attacker.equals(victim) || event.isCancelled()) {
                return;
            }

            if (this.config.displayCombatPredictionAttacker || this.config.displayCombatPredictionVictim) {
                this.sendCombatPrediction(attackerUser, victimUser);
            }

            if (!this.config.assistEnable) {
                return;
            }

            if (HookManager.WORLD_GUARD.map(worldGuard -> worldGuard.isInNonAssistsRegion(victim.getLocation()))
                    .orElseGet(false)) {
                return;
            }

            DamageState damageState = this.damageManager.getDamageState(victimUser.getUUID());
            damageState.addDamage(attackerUser, event.getDamage());
        });
    }

    private void sendCombatPrediction(User attacker, User victim) {
        PredictionKey key = new PredictionKey(attacker.getUUID(), victim.getUUID());
        if (this.predictionCooldowns.getIfPresent(key) != null) {
            return;
        }
        this.predictionCooldowns.put(key, Boolean.TRUE);

        RankSystem.RankResult predicted = this.rankSystem.calculate(
                this.config.rankSystem,
                attacker.getRank().getPoints(),
                victim.getRank().getPoints()
        );

        if (this.config.displayCombatPredictionAttacker) {
            int change = predicted.getAttackerPoints();
            this.messageService.getMessage(config -> config.combatPredictionAttackerMessage)
                    .with("{VICTIM}", victim.getName())
                    .with("{ATTACKER-CHANGE}", change)
                    .with("{ATTACKER-CHANGE-FORMATTED}", RankFormatter.formatPointsChange(change, this.config.killPointsChangeFormat))
                    .receiver(attacker)
                    .send();
        }

        if (this.config.displayCombatPredictionVictim) {
            int change = -predicted.getVictimPoints();
            this.messageService.getMessage(config -> config.combatPredictionVictimMessage)
                    .with("{ATTACKER}", attacker.getName())
                    .with("{VICTIM-CHANGE}", change)
                    .with("{VICTIM-CHANGE-FORMATTED}", RankFormatter.formatPointsChange(change, this.config.killPointsChangeFormat))
                    .receiver(victim)
                    .send();
        }
    }

    private record PredictionKey(UUID attacker, UUID victim) {
    }
}
