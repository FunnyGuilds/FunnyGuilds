package net.dzikoysk.funnyguilds.listener;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.util.UUID;
import java.util.Optional;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.panda_lang.utilities.inject.annotations.Inject;

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

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        EntityUtils.getDamageAttacker(event).ifPresent(attacker -> {
            Optional<User> attackerUserOption = this.userManager.findByPlayer(attacker);
            if (attackerUserOption.isEmpty()) {
                return;
            }

            User attackerUser = attackerUserOption.orElseThrow();
            Entity victim = event.getEntity();

            if (this.config.animalsProtection && (victim instanceof Animals || victim instanceof Villager)) {
                this.regionManager.findRegionAtLocation(victim.getLocation())
                        .map(Region::getGuild)
                        .filter(guild -> !attackerUser.getGuild()
                                .map(it -> it.equals(guild))
                                .orElse(false))
                        .ifPresent(guild -> event.setCancelled(true));

                return;
            }

            Optional<User> victimOption = Optional.ofNullable(victim)
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .flatMap(this.userManager::findByPlayer);

            if (victimOption.isEmpty()) {
                return;
            }

            User victimUser = victimOption.orElseThrow();
            if (victimUser.hasGuild() && attackerUser.hasGuild()) {
                if (victimUser.getUUID().equals(attackerUser.getUUID())) {
                    return;
                }

                Guild victimGuild = victimUser.getGuild().orElseThrow();
                Guild attackerGuild = attackerUser.getGuild().orElseThrow();

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
                        .or(() -> Optional.of(FriendlyFireStatus.INHERIT))
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
                        .orElseThrow();

                if (shouldReturn) {
                    return;
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageMonitor(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player victim)) {
            return;
        }

        EntityUtils.getDamageAttacker(event).ifPresent(attacker -> {
            if (attacker.equals(victim)) {
                return;
            }

            Optional<User> attackerOption = this.userManager.findByPlayer(attacker);
            Optional<User> victimOption = this.userManager.findByPlayer(victim);
            if (attackerOption.isEmpty() || victimOption.isEmpty()) {
                return;
            }

            User attackerUser = attackerOption.orElseThrow();
            User victimUser = victimOption.orElseThrow();

            if (this.config.displayCombatPredictionAttacker || this.config.displayCombatPredictionVictim) {
                this.sendCombatPrediction(attackerUser, victimUser);
            }

            if (!this.config.assistEnable) {
                return;
            }

            if (HookManager.WORLD_GUARD.map(worldGuard -> worldGuard.isInNonAssistsRegion(victim.getLocation()))
                    .orElse(false)) {
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
