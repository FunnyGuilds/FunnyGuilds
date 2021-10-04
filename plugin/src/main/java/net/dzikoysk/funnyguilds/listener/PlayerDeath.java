package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateUserPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.config.IntegerRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration.DataModel;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.RankChangeEvent;
import net.dzikoysk.funnyguilds.feature.hooks.PluginHook;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.shared.MapUtil;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class PlayerDeath extends AbstractFunnyListener {

    private final RankSystem rankSystem;

    public PlayerDeath(FunnyGuilds plugin) {
        this.rankSystem = RankSystem.create(plugin.getPluginConfiguration());
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

        victim.getRank().updateDeaths(currentValue -> currentValue + 1);

        if (playerAttacker == null) {
            if (!config.considerLastAttackerAsKiller) {
                victimCache.clearDamage();
                return;
            }

            User lastAttacker = victimCache.getLastKiller();

            if (lastAttacker == null || !lastAttacker.isOnline()) {
                victimCache.clearDamage();
                return;
            }

            Long attackTime = victimCache.wasVictimOf(lastAttacker);

            if (attackTime == null || attackTime + config.lastAttackerAsKillerConsiderationTimeout_ < System.currentTimeMillis()) {
                victimCache.clearDamage();
                return;
            }

            playerAttacker = lastAttacker.getPlayer();
        }

        Option<User> attackerOption = this.userManager.findByPlayer(playerAttacker);
        if(attackerOption.isEmpty()) {
            return;
        }
        User attacker = attackerOption.get();
        UserCache attackerCache = attacker.getCache();

        if (victim.equals(attacker)) {
            victimCache.clearDamage();
            return;
        }

        if (PluginHook.isPresent(PluginHook.PLUGIN_WORLDGUARD)) {
            if (PluginHook.WORLD_GUARD.isInNonPointsRegion(playerVictim.getLocation()) || PluginHook.WORLD_GUARD.isInNonPointsRegion(playerAttacker.getLocation())) {
                victimCache.clearDamage();
                return;
            }
        }

        if (config.rankFarmingProtect) {
            Long attackTimestamp = attackerCache.wasAttackerOf(victim);
            Long victimTimestamp = attackerCache.wasVictimOf(attacker);

            if (attackTimestamp != null) {
                if (attackTimestamp + (config.rankFarmingCooldown * 1000L) >= System.currentTimeMillis()) {
                    playerVictim.sendMessage(messages.rankLastVictimV);
                    playerAttacker.sendMessage(messages.rankLastVictimA);

                    victimCache.clearDamage();
                    event.setDeathMessage(null);

                    return;
                }
            } else if (victimTimestamp != null) {
                if (victimTimestamp + (config.rankFarmingCooldown * 1000L) >= System.currentTimeMillis()) {
                    playerVictim.sendMessage(messages.rankLastAttackerV);
                    playerAttacker.sendMessage(messages.rankLastAttackerA);

                    victimCache.clearDamage();
                    event.setDeathMessage(null);

                    return;
                }
            }
        }

        if (config.rankIPProtect) {
            String attackerIP = playerAttacker.getAddress().getHostString();

            if (attackerIP != null && attackerIP.equalsIgnoreCase(playerVictim.getAddress().getHostString())) {
                playerVictim.sendMessage(messages.rankIPVictim);
                playerAttacker.sendMessage(messages.rankIPAttacker);

                victimCache.clearDamage();
                event.setDeathMessage(null);

                return;
            }
        }

        int[] rankChanges = new int[2];
        int victimPoints = victim.getRank().getPoints();
        int attackerPoints = attacker.getRank().getPoints();

        RankSystem.RankResult result = rankSystem.calculate(config.rankSystem, attackerPoints, victimPoints);

        RankChangeEvent attackerEvent = new PointsChangeEvent(EventCause.USER, attacker.getRank(), attacker, result.getAttackerPoints());
        RankChangeEvent victimEvent = new PointsChangeEvent(EventCause.USER, victim.getRank(), attacker, result.getVictimPoints());

        List<String> assistEntries = new ArrayList<>();
        List<User> messageReceivers = new ArrayList<>();

        int victimPointsBeforeChange = victim.getRank().getPoints();

        if (SimpleEventHandler.handle(attackerEvent) && SimpleEventHandler.handle(victimEvent)) {
            double attackerDamage = victimCache.killedBy(attacker);

            if (config.assistEnable && victimCache.isAssisted()) {
                double toShare = attackerEvent.getChange() * (1 - config.assistKillerShare);
                double totalDamage = victimCache.getTotalDamage() + attackerDamage;
                int givenPoints = 0;

                Map<User, Double> damage = MapUtil.sortByValue(victimCache.getDamage());
                int assists = 0;

                for (Entry<User, Double> assist : damage.entrySet()) {
                    User assistUser = assist.getKey();
                    double assistFraction = assist.getValue() / totalDamage;
                    int addedPoints = (int) Math.round(assistFraction * toShare);

                    if (addedPoints <= 0) {
                        continue;
                    }

                    if (config.assistsLimit > 0) {
                        if (assists >= config.assistsLimit) {
                            continue;
                        }

                        assists++;
                    }

                    if (!config.broadcastDeathMessage) {
                        messageReceivers.add(assistUser);
                    }

                    givenPoints += addedPoints;

                    String assistEntry = StringUtils.replace(messages.rankAssistEntry, "{PLAYER}", assistUser.getName());
                    assistEntry = StringUtils.replace(assistEntry, "{+}", Integer.toString(addedPoints));
                    assistEntry = StringUtils.replace(assistEntry, "{SHARE}", ChatUtils.getPercent(assistFraction));
                    assistEntries.add(assistEntry);

                    assistUser.getRank().updatePoints(currentValue -> currentValue + addedPoints);
                    assistUser.getRank().updateAssists(currentValue -> currentValue + 1);
                }

                double updatedAttackerPoints = attackerEvent.getChange() - toShare + (givenPoints < toShare ? toShare - givenPoints : 0);
                attackerEvent.setChange((int) Math.round(updatedAttackerPoints));
            }

            attacker.getRank().updateKills(currentValue -> currentValue + 1);
            attacker.getRank().updatePoints(currentValue -> currentValue + attackerEvent.getChange());
            attackerCache.registerVictim(victim);

            victimPointsBeforeChange = victim.getRank().getPoints();

            victim.getRank().updatePoints(currentValue -> currentValue - victimEvent.getChange());
            victimCache.registerKiller(attacker);
            victimCache.clearDamage();

            if (!config.broadcastDeathMessage) {
                messageReceivers.add(attacker);
                messageReceivers.add(victim);
            }
        }

        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

        if (config.dataModel == DataModel.MYSQL) {
            if (victim.hasGuild()) {
                taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(victim.getGuild()));
            }

            if (attacker.hasGuild()) {
                taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(attacker.getGuild()));
            }

            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(victim));
            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(attacker));
        }

        this.concurrencyManager.postTask(taskBuilder
                .delegate(new DummyGlobalUpdateUserRequest(victim))
                .delegate(new DummyGlobalUpdateUserRequest(attacker))
                .build());

        Formatter killFormatter = new Formatter()
                .register("{ATTACKER}", attacker.getName())
                .register("{VICTIM}", victim.getName())
                .register("{+}", Integer.toString(attackerEvent.getChange()))
                .register("{-}", Math.min(victimPointsBeforeChange, victimEvent.getChange()))
                .register("{POINTS-FORMAT}", IntegerRange.inRangeToString(victimPoints, config.pointsFormat))
                .register("{POINTS}", Integer.toString(victim.getRank().getPoints()))
                .register("{WEAPON}", MaterialUtils.getMaterialName(playerAttacker.getItemInHand().getType()))
                .register("{WEAPON-NAME}", MaterialUtils.getItemCustomName(playerAttacker.getItemInHand()))
                .register("{REMAINING-HEALTH}", String.format(Locale.US, "%.2f", playerAttacker.getHealth()))
                .register("{REMAINING-HEARTS}", Integer.toString((int) (playerAttacker.getHealth() / 2)))
                .register("{VTAG}", victim.hasGuild()
                        ? StringUtils.replace(config.chatGuild, "{TAG}", victim.getGuild().getTag())
                        : "")
                .register("{ATAG}", attacker.hasGuild()
                        ? StringUtils.replace(config.chatGuild, "{TAG}", attacker.getGuild().getTag())
                        : "")
                .register("{ASSISTS}", config.assistEnable && !assistEntries.isEmpty()
                        ? StringUtils.replace(messages.rankAssistMessage, "{ASSISTS}", String.join(messages.rankAssistDelimiter, assistEntries))
                        : "");

        if (config.displayTitleNotificationForKiller) {
            TitleMessage titleMessage = TitleMessage.builder()
                    .text(killFormatter.format(messages.rankKillTitle))
                    .subText(killFormatter.format(messages.rankKillSubtitle))
                    .fadeInDuration(config.notificationTitleFadeIn)
                    .stayDuration(config.notificationTitleStay)
                    .fadeOutDuration(config.notificationTitleFadeOut)
                    .build();

            plugin.getNmsAccessor().getMessageAccessor().sendTitleMessage(titleMessage, playerAttacker);
        }

        String deathMessage = killFormatter.format(messages.rankDeathMessage);

        if (config.broadcastDeathMessage) {
            if (config.ignoreDisabledDeathMessages) {
                for (Player player : event.getEntity().getWorld().getPlayers()) {
                    event.setDeathMessage(null);
                    player.sendMessage(deathMessage);
                }
            } else {
                event.setDeathMessage(deathMessage);
            }
        } else {
            event.setDeathMessage(null);

            for (User fighter : messageReceivers) {
                if (fighter.isOnline()) {
                    fighter.getPlayer().sendMessage(deathMessage);
                }
            }
        }

    }

    private int[] getEloValues(int victimPoints, int attackerPoints) {
        int[] rankChanges = new int[2];

        int attackerElo = IntegerRange.inRange(attackerPoints, config.eloConstants).orElseGet(0);
        int victimElo = IntegerRange.inRange(victimPoints, config.eloConstants).orElseGet(0);

        rankChanges[0] = (int) Math.round(attackerElo * (1 - (1.0D / (1.0D + Math.pow(config.eloExponent, (victimPoints - attackerPoints) / config.eloDivider)))));
        rankChanges[1] = (int) Math.round(victimElo * (0 - (1.0D / (1.0D + Math.pow(config.eloExponent, (attackerPoints - victimPoints) / config.eloDivider)))) * -1);

        return rankChanges;
    }

}
