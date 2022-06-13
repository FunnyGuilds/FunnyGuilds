package net.dzikoysk.funnyguilds.listener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateUserPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration.DataModel;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.AssistsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.DeathsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.KillsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuardHook;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.MapUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import panda.std.Option;

public class PlayerDeath extends AbstractFunnyListener {

    private final RankSystem rankSystem;

    public PlayerDeath(PluginConfiguration config) {
        this.rankSystem = RankSystem.create(config);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player playerVictim = event.getEntity();
        Player playerAttacker = event.getEntity().getKiller();

        Option<User> victimOption = this.userManager.findByPlayer(playerVictim);
        if (victimOption.isEmpty()) {
            return;
        }

        User victim = victimOption.get();
        UserCache victimCache = victim.getCache();

        DeathsChangeEvent deathsChangeEvent = new DeathsChangeEvent(EventCause.USER, victim, victim, 1);
        if (SimpleEventHandler.handle(deathsChangeEvent)) {
            victim.getRank().updateDeaths(currentValue -> currentValue + deathsChangeEvent.getDeathsChange());
        }

        if (playerAttacker == null) {
            if (!this.config.considerLastAttackerAsKiller) {
                victimCache.clearDamage();
                return;
            }

            User lastAttacker = victimCache.getLastKiller();
            if (lastAttacker == null || !lastAttacker.isOnline()) {
                victimCache.clearDamage();
                return;
            }

            Instant attackTime = victimCache.wasVictimOf(lastAttacker);
            if (attackTime == null || attackTime.plus(this.config.lastAttackerAsKillerConsiderationTimeout).isBefore(Instant.now())) {
                victimCache.clearDamage();
                return;
            }

            playerAttacker = this.server.getPlayer(lastAttacker.getUUID());
        }

        Option<User> attackerOption = this.userManager.findByPlayer(playerAttacker);
        if (attackerOption.isEmpty()) {
            return;
        }

        User attacker = attackerOption.get();
        UserCache attackerCache = attacker.getCache();

        if (victim.equals(attacker)) {
            victimCache.clearDamage();
            return;
        }

        if (HookManager.WORLD_GUARD.isPresent()) {
            WorldGuardHook worldGuard = HookManager.WORLD_GUARD.get();
            if (worldGuard.isInNonPointsRegion(playerVictim.getLocation()) || worldGuard.isInNonPointsRegion(playerAttacker.getLocation())) {
                victimCache.clearDamage();
                return;
            }
        }

        if (this.config.rankFarmingProtect) {
            Instant attackTimestamp = attackerCache.wasAttackerOf(victim);
            Instant victimTimestamp = attackerCache.wasVictimOf(attacker);

            if (attackTimestamp != null) {
                if (attackTimestamp.plus(this.config.rankFarmingCooldown).compareTo(Instant.now()) >= 0) {
                    ChatUtils.sendMessage(playerVictim, this.messages.rankLastVictimV);
                    ChatUtils.sendMessage(playerAttacker, this.messages.rankLastVictimA);

                    victimCache.clearDamage();
                    event.setDeathMessage(null);

                    return;
                }
            }
            else if (victimTimestamp != null) {
                if (victimTimestamp.plus(this.config.rankFarmingCooldown).compareTo(Instant.now()) >= 0) {
                    ChatUtils.sendMessage(playerVictim, this.messages.rankLastAttackerV);
                    ChatUtils.sendMessage(playerAttacker, this.messages.rankLastAttackerA);

                    victimCache.clearDamage();
                    event.setDeathMessage(null);

                    return;
                }
            }
        }

        if (this.config.rankIPProtect) {
            String attackerIP = playerAttacker.getAddress().getHostString();

            if (attackerIP != null && attackerIP.equalsIgnoreCase(playerVictim.getAddress().getHostString())) {
                ChatUtils.sendMessage(playerVictim, this.messages.rankIPVictim);
                ChatUtils.sendMessage(playerAttacker, this.messages.rankIPAttacker);

                victimCache.clearDamage();
                event.setDeathMessage(null);

                return;
            }
        }

        int victimPoints = victim.getRank().getPoints();
        int attackerPoints = attacker.getRank().getPoints();

        RankSystem.RankResult result = this.rankSystem.calculate(this.config.rankSystem, attackerPoints, victimPoints);

        PointsChangeEvent attackerPointsChangeEvent = new PointsChangeEvent(EventCause.USER, attacker, attacker, result.getAttackerPoints());
        PointsChangeEvent victimPointsChangeEvent = new PointsChangeEvent(EventCause.USER, attacker, victim, -result.getVictimPoints());

        List<String> assistEntries = new ArrayList<>();
        List<User> messageReceivers = new ArrayList<>();

        int victimPointsBeforeChange = victim.getRank().getPoints();

        if (SimpleEventHandler.handle(attackerPointsChangeEvent) && SimpleEventHandler.handle(victimPointsChangeEvent)) {
            double attackerDamage = victimCache.killedBy(attacker);

            if (this.config.assistEnable && victimCache.isAssisted()) {
                double toShare = attackerPointsChangeEvent.getPointsChange() * (1 - this.config.assistKillerShare);
                double totalDamage = victimCache.getTotalDamage() + attackerDamage;
                int givenPoints = 0;

                Map<User, Double> damage = MapUtils.sortByValue(victimCache.getDamage());
                int assists = 0;

                for (Entry<User, Double> assist : damage.entrySet()) {
                    User assistUser = assist.getKey();
                    double assistFraction = assist.getValue() / totalDamage;
                    int addedPoints = (int) Math.round(assistFraction * toShare);

                    if (addedPoints <= 0) {
                        continue;
                    }

                    if (this.config.assistsLimit > 0) {
                        if (assists >= this.config.assistsLimit) {
                            break;
                        }

                        assists++;
                    }

                    PointsChangeEvent assistPointsChangeEvent = new PointsChangeEvent(EventCause.USER, assistUser, assistUser, addedPoints);
                    if (!SimpleEventHandler.handle(assistPointsChangeEvent)) {
                        continue;
                    }

                    if (!this.config.broadcastDeathMessage) {
                        messageReceivers.add(assistUser);
                    }

                    addedPoints = assistPointsChangeEvent.getPointsChange();
                    givenPoints += addedPoints;

                    FunnyFormatter formatter = new FunnyFormatter()
                            .register("{PLAYER}", assistUser.getName())
                            .register("{+}", addedPoints)
                            .register("{SHARE}", FunnyStringUtils.getPercent(assistFraction));

                    assistEntries.add(formatter.format(this.messages.rankAssistEntry));

                    int finalAddedPoints = addedPoints;
                    assistUser.getRank().updatePoints(currentValue -> currentValue + finalAddedPoints);

                    AssistsChangeEvent assistsChangeEvent = new AssistsChangeEvent(EventCause.USER, victim, assistUser, 1);
                    if (SimpleEventHandler.handle(assistsChangeEvent)) {
                        assistUser.getRank().updateAssists(currentValue -> currentValue + assistsChangeEvent.getAssistsChange());
                    }
                }

                double updatedAttackerPoints = attackerPointsChangeEvent.getPointsChange() - toShare + (givenPoints < toShare ? toShare - givenPoints : 0);
                attackerPointsChangeEvent.setPointsChange((int) Math.round(updatedAttackerPoints));
            }

            attacker.getRank().updatePoints(currentValue -> currentValue + attackerPointsChangeEvent.getPointsChange());

            KillsChangeEvent killsChangeEvent = new KillsChangeEvent(EventCause.USER, attacker, victim, 1);
            if (SimpleEventHandler.handle(killsChangeEvent)) {
                attacker.getRank().updateKills(currentValue -> currentValue + killsChangeEvent.getKillsChange());
            }

            attackerCache.registerVictim(victim);
            victimPointsBeforeChange = victim.getRank().getPoints();

            victim.getRank().updatePoints(currentValue -> currentValue + victimPointsChangeEvent.getPointsChange());
            victimCache.registerKiller(attacker);
            victimCache.clearDamage();

            if (!this.config.broadcastDeathMessage) {
                messageReceivers.add(attacker);
                messageReceivers.add(victim);
            }
        }

        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();
        if (this.config.dataModel == DataModel.MYSQL) {
            victim.getGuild().peek(guild -> taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(guild)));
            attacker.getGuild().peek(guild -> taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(guild)));

            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(victim));
            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(attacker));
        }

        this.concurrencyManager.postTask(taskBuilder
                .delegate(new DummyGlobalUpdateUserRequest(victim))
                .delegate(new DummyGlobalUpdateUserRequest(attacker))
                .build());

        int attackerPointsChange = attackerPointsChangeEvent.getPointsChange();
        int victimPointsChange = Math.min(victimPointsBeforeChange, victimPointsChangeEvent.getPointsChange());

        FunnyFormatter killFormatter = new FunnyFormatter()
                .register("{ATTACKER}", attacker.getName())
                .register("{VICTIM}", victim.getName())
                .register("{+}", attackerPointsChange)
                .register("{-}", victimPointsChange)
                .register("{PLUS-FORMATTED}", NumberRange.inRangeToString(attackerPointsChange, this.config.killPointsChangeFormat, true))
                .register("{CHANGE}", Math.abs(attackerPointsChange))
                .register("{MINUS-FORMATTED}", NumberRange.inRangeToString(victimPointsChange, this.config.killPointsChangeFormat, true))
                .register("{CHANGE}", Math.abs(victimPointsChange))
                .register("{POINTS-FORMAT}", NumberRange.inRangeToString(victimPoints, this.config.pointsFormat, true))
                .register("{POINTS}", victim.getRank().getPoints())
                .register("{WEAPON}", MaterialUtils.getMaterialName(playerAttacker.getItemInHand().getType()))
                .register("{WEAPON-NAME}", MaterialUtils.getItemCustomName(playerAttacker.getItemInHand()))
                .register("{REMAINING-HEALTH}", String.format(Locale.US, "%.2f", playerAttacker.getHealth()))
                .register("{REMAINING-HEARTS}", (int) (playerAttacker.getHealth() / 2))
                .register("{VTAG}", victim.getGuild()
                        .map(guild -> FunnyFormatter.format(this.config.chatGuild.getValue(), "{TAG}", guild.getTag()))
                        .orElseGet(""))
                .register("{ATAG}", attacker.getGuild()
                        .map(guild -> FunnyFormatter.format(this.config.chatGuild.getValue(), "{TAG}", guild.getTag()))
                        .orElseGet(""))
                .register("{ASSISTS}", this.config.assistEnable && !assistEntries.isEmpty()
                        ? FunnyFormatter.format(this.messages.rankAssistMessage, "{ASSISTS}", String.join(this.messages.rankAssistDelimiter, assistEntries))
                        : "");

        if (this.config.displayTitleNotificationForKiller) {
            TitleMessage titleMessage = TitleMessage.builder()
                    .text(killFormatter.format(this.messages.rankKillTitle))
                    .subText(killFormatter.format(this.messages.rankKillSubtitle))
                    .fadeInDuration(this.config.notificationTitleFadeIn)
                    .stayDuration(this.config.notificationTitleStay)
                    .fadeOutDuration(this.config.notificationTitleFadeOut)
                    .build();

            this.messageAccessor.sendTitleMessage(titleMessage, playerAttacker);
        }

        String deathMessage = killFormatter.format(this.messages.rankDeathMessage);

        if (this.config.broadcastDeathMessage) {
            if (this.config.ignoreDisabledDeathMessages) {
                event.getEntity().getWorld().getPlayers().forEach(player -> {
                    event.setDeathMessage(null);
                    ChatUtils.sendMessage(player, deathMessage);
                });
            }
            else {
                event.setDeathMessage(deathMessage);
            }
        }
        else {
            event.setDeathMessage(null);
            messageReceivers.forEach(fighter -> fighter.sendMessage(deathMessage));
        }

    }

}
