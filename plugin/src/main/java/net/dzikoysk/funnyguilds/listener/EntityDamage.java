package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.damage.DamageManager;
import net.dzikoysk.funnyguilds.damage.DamageState;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuardHook.FriendlyFireStatus;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.EntityUtils;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.kyori.adventure.text.Component;
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

            if (attacker.equals(victim)) {
                return;
            }

            if (!this.config.assistEnable || event.isCancelled()) {
                return;
            }

            if (HookManager.WORLD_GUARD.map(worldGuard -> worldGuard.isInNonAssistsRegion(victim.getLocation()))
                    .orElseGet(false)) {
                return;
            }

            DamageState damageState = this.damageManager.getDamageState(victimUser.getUUID());
            double previousDamage = damageState.getTotalDamage(attackerUser);

            if (this.config.displayCombatPredictionAttacker || this.config.displayCombatPredictionVictim) {
                // damage history is wiped after 1 minute, so if we haven't landed a hit during 1 minute,
                // let's consider this a good moment to remind players what point could they get.
                if (previousDamage == 0.0) {
                    int attackerPoints = attackerUser.getRank().getPoints();
                    int victimPoints = victimUser.getRank().getPoints();

                    RankSystem.RankResult predictedResult = this.rankSystem.calculate(
                            this.config.rankSystem,
                            attackerPoints,
                            victimPoints
                    );

                    if (this.config.displayCombatPredictionAttacker) {
                        int predictedGain = predictedResult.getAttackerPoints();

                        FunnyFormatter attackerFormatter = new FunnyFormatter()
                                .register("{VICTIM}", victimUser.getName())
                                .register("{+}", predictedGain)
                                .register("{PLUS-FORMATTED}", formatChangeWithRange(predictedGain))
                                .register("{CHANGE}", Math.abs(predictedGain))
                                .register("{POINTS-FORMATTED}", formatChangeWithRange(predictedGain));

                        this.messageService.getMessage(config -> config.combatPredictionAttackerMessage)
                                .with(attackerFormatter)
                                .receiver(attackerUser)
                                .send();
                    }

                    if (this.config.displayCombatPredictionVictim) {
                        int predictedLoss = -predictedResult.getVictimPoints();

                        FunnyFormatter victimFormatter = new FunnyFormatter()
                                .register("{ATTACKER}", attackerUser.getName())
                                .register("{-}", predictedLoss)
                                .register("{MINUS-FORMATTED}", formatChangeWithRange(predictedLoss))
                                .register("{CHANGE}", Math.abs(predictedLoss))
                                .register("{POINTS-FORMATTED}", formatChangeWithRange(predictedLoss));

                        this.messageService.getMessage(config -> config.combatPredictionVictimMessage)
                                .with(victimFormatter)
                                .receiver(victimUser)
                                .send();
                    }
                }
            }

            damageState.addDamage(attackerUser, event.getDamage());
        });
    }

    private Component formatChangeWithRange(int change) {
        String format = NumberRange.inRangeToString(change, this.config.killPointsChangeFormat, true);
        String value = FunnyFormatter.format(format, "{CHANGE}", Math.abs(change));
        return ChatUtils.deserializeSection(value);
    }

}
