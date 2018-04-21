package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateUserPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateUserRequest;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.RankChangeEvent;
import net.dzikoysk.funnyguilds.hook.PluginHook;
import net.dzikoysk.funnyguilds.hook.WorldGuardHook;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.commons.MapUtil;
import net.dzikoysk.funnyguilds.util.commons.StringUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PlayerDeath implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player v = event.getEntity();
        Player a = event.getEntity().getKiller();

        User victim = User.get(v);
        victim.getRank().addDeath();

        if (a == null) {
            return;
        }

        User attacker = User.get(a);
        if (victim.equals(attacker)) {
            return;
        }

        if (PluginHook.isPresent(PluginHook.PLUGIN_WORLDGUARD)) {
            if (WorldGuardHook.isInNonPointsRegion(v.getLocation()) || WorldGuardHook.isInNonPointsRegion(a.getLocation())) {
                return;
            }
        }

        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();
        
        if (config.rankFarmingProtect) {
            if (attacker.getLastVictim() != null && attacker.getLastVictim().equals(victim)) {
                if (attacker.getLastVictimTime() + (config.rankFarmingCooldown * 1000) >= System.currentTimeMillis()) {
                    v.sendMessage(messages.rankLastVictimV);
                    a.sendMessage(messages.rankLastVictimA);
                    event.setDeathMessage(null);
                    return;
                }
            } else if (victim.getLastAttacker() != null && victim.getLastAttacker().equals(attacker)) {
                if (victim.getLastVictimTime() + (config.rankFarmingCooldown * 1000) >= System.currentTimeMillis()) {
                    v.sendMessage(messages.rankLastAttackerV);
                    a.sendMessage(messages.rankLastAttackerA);
                    event.setDeathMessage(null);
                    return;
                }
            }
        }
        
        if (config.rankIPProtect) {
            if (a.getAddress().getHostString().equalsIgnoreCase(v.getAddress().getHostString())) {
                v.sendMessage(messages.rankIPVictim);
                a.sendMessage(messages.rankIPAttacker);
                event.setDeathMessage(null);
                return;
            }
        }

        int[] rankChanges = new int[2];
        int aP = attacker.getRank().getPoints();
        int vP = victim.getRank().getPoints();

        switch (config.rankSystem) {
            case ELO:
                rankChanges = getEloValues(vP, aP);
                break;
            case PERCENT:
                Double d = victim.getRank().getPoints() * (config.percentRankChange / 100);
                rankChanges[0] = d.intValue();
                rankChanges[1] = d.intValue();
                break;
            case STATIC:
                rankChanges[0] = config.staticAttackerChange;
                rankChanges[1] = config.staticVictimChange;
                break;
            default:
                rankChanges = getEloValues(vP, aP);
                break;
        }

        RankChangeEvent attackerEvent = new PointsChangeEvent(EventCause.USER, attacker.getRank(), attacker, rankChanges[0]);
        RankChangeEvent victimEvent = new PointsChangeEvent(EventCause.USER, victim.getRank(), attacker, rankChanges[1]);
        
        List<String> assistEntries = new ArrayList<>();
        if (SimpleEventHandler.handle(attackerEvent) && SimpleEventHandler.handle(victimEvent)) {
            double attackerDamage = victim.killedBy(attacker);
            
            if (config.assistEnable && victim.isAssisted()) {
                double toShare = attackerEvent.getChange() * (1 - config.assistKillerShare);
                double totalDamage = victim.getTotalDamage() + attackerDamage;
                int givenPoints = 0;

                Map<User, Double> damage = MapUtil.sortByValue(victim.getDamage());

                int assists = 0;

                for (Entry<User, Double> assist : damage.entrySet()) {
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

                    givenPoints += addedPoints;
                    
                    String assistEntry = StringUtils.replace(messages.rankAssistEntry, "{PLAYER}", assist.getKey().getName());
                    assistEntry = StringUtils.replace(assistEntry, "{+}", Integer.toString(addedPoints));
                    assistEntry = StringUtils.replace(assistEntry, "{SHARE}", StringUtils.getPercent(assistFraction));
                    assistEntries.add(assistEntry);
                    assist.getKey().getRank().addPoints(addedPoints);
                }
                
                double attackerPoints = attackerEvent.getChange() - toShare + (givenPoints < toShare ? toShare - givenPoints : 0);
                attackerEvent.setChange((int) Math.round(attackerPoints));
            }
            
            attacker.getRank().addKill();
            attacker.getRank().addPoints(attackerEvent.getChange());
            attacker.setLastVictim(victim);

            victim.getRank().removePoints(victimEvent.getChange());
            victim.setLastAttacker(attacker);
            victim.clearDamage();
        }

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

        if (config.dataType.mysql) {
            if (victim.hasGuild()) {
                // IndependentThread.actions(ActionType.MYSQL_UPDATE_GUILD_POINTS, victim.getGuild());
                taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(victim.getGuild()));
            }

            if (attacker.hasGuild()) {
                // IndependentThread.actions(ActionType.MYSQL_UPDATE_GUILD_POINTS, attacker.getGuild());
                taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(attacker.getGuild()));
            }

            // IndependentThread.actions(ActionType.MYSQL_UPDATE_USER_POINTS, victim);
            // IndependentThread.actions(ActionType.MYSQL_UPDATE_USER_POINTS, attacker);
            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(victim));
            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(attacker));
        }

        /*
        IndependentThread.actions(ActionType.DUMMY_GLOBAL_UPDATE_USER, victim);
        IndependentThread.actions(ActionType.DUMMY_GLOBAL_UPDATE_USER, attacker);
        IndependentThread.actions(ActionType.RANK_UPDATE_USER, victim);
        IndependentThread.action(ActionType.RANK_UPDATE_USER, attacker);
        */

        ConcurrencyTask task = taskBuilder
                .delegate(new DummyGlobalUpdateUserRequest(victim))
                .delegate(new DummyGlobalUpdateUserRequest(attacker))
                .delegate(new RankUpdateUserRequest(victim))
                .delegate(new RankUpdateUserRequest(attacker))
                .build();
        concurrencyManager.postTask(task);

        String deathMessage = messages.rankDeathMessage;
        deathMessage = StringUtils.replace(deathMessage, "{ATTACKER}", attacker.getName());
        deathMessage = StringUtils.replace(deathMessage, "{VICTIM}", victim.getName());
        deathMessage = StringUtils.replace(deathMessage, "{+}", Integer.toString(attackerEvent.getChange()));
        deathMessage = StringUtils.replace(deathMessage, "{-}", Integer.toString(victimEvent.getChange()));
        deathMessage = StringUtils.replace(deathMessage, "{POINTS-FORMAT}", IntegerRange.inRange(vP, config.pointsFormat, "POINTS"));
        deathMessage = StringUtils.replace(deathMessage, "{POINTS}", String.valueOf(victim.getRank().getPoints()));
        deathMessage = StringUtils.replace(deathMessage, "{WEAPON}", MaterialUtil.getMaterialName(a.getItemInHand().getType()));
        deathMessage = StringUtils.replace(deathMessage, "{REMAINING-HEALTH}", Double.toString(a.getHealth()));
        deathMessage = StringUtils.replace(deathMessage, "{REMAINING-HEARTS}", Double.toString(a.getHealth() / 2));

        if (victim.hasGuild()) {
            deathMessage = StringUtils.replace(deathMessage, "{VTAG}", StringUtils.replace(config.chatGuild, "{TAG}", victim.getGuild().getTag()));
        }

        if (attacker.hasGuild()) {
            deathMessage = StringUtils.replace(deathMessage, "{ATAG}", StringUtils.replace(config.chatGuild, "{TAG}", attacker.getGuild().getTag()));
        }

        deathMessage = StringUtils.replace(deathMessage, "{VTAG}", "");
        deathMessage = StringUtils.replace(deathMessage, "{ATAG}", "");
        
        event.setDeathMessage(deathMessage);
    }
	
    private int[] getEloValues(int vP, int aP) {
        PluginConfig config = Settings.getConfig();
        int[] rankChanges = new int[2];
        
        int aC = IntegerRange.inRange(aP, config.eloConstants, "ELO_CONSTANTS");
        int vC = IntegerRange.inRange(vP, config.eloConstants, "ELO_CONSTANTS");
        
        rankChanges[0] = (int) Math.round(aC * (1 - (1.0D / (1.0D + Math.pow(config.eloExponent, (vP - aP) / config.eloDivider)))));
        rankChanges[1] = (int) Math.round(vC * (0 - (1.0D / (1.0D + Math.pow(config.eloExponent, (aP - vP) / config.eloDivider)))) * -1);
        
        return rankChanges;
    }
	
}
