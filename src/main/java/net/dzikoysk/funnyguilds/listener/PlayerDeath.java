package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateUserPointsRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateUserRequest;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration.DataType;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.RankChangeEvent;
import net.dzikoysk.funnyguilds.hook.PluginHook;
import net.dzikoysk.funnyguilds.hook.WorldGuardHook;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.MapUtil;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class PlayerDeath implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player v = event.getEntity();
        Player a = event.getEntity().getKiller();

        User victim = User.get(v);
        UserCache victimCache = victim.getCache();

        victim.getRank().addDeath();

        if (a == null) {
            victimCache.clearDamage();
            return;
        }

        User attacker = User.get(a);
        UserCache attackerCache = attacker.getCache();

        if (victim.equals(attacker)) {
            victimCache.clearDamage();
            return;
        }

        if (PluginHook.isPresent(PluginHook.PLUGIN_WORLDGUARD)) {
            if (WorldGuardHook.isInNonPointsRegion(v.getLocation()) || WorldGuardHook.isInNonPointsRegion(a.getLocation())) {
                victimCache.clearDamage();
                return;
            }
        }

        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        
        if (config.rankFarmingProtect) {
            if (attackerCache.getLastVictim() != null && attackerCache.getLastVictim().equals(victim)) {
                if (attackerCache.getLastVictimTime() + (config.rankFarmingCooldown * 1000) >= System.currentTimeMillis()) {
                    v.sendMessage(messages.rankLastVictimV);
                    a.sendMessage(messages.rankLastVictimA);
                    
                    victimCache.clearDamage();
                    event.setDeathMessage(null);
                    
                    return;
                }
            } else if (victimCache.getLastAttacker() != null && victimCache.getLastAttacker().equals(attacker)) {
                if (victimCache.getLastVictimTime() + (config.rankFarmingCooldown * 1000) >= System.currentTimeMillis()) {
                    v.sendMessage(messages.rankLastAttackerV);
                    a.sendMessage(messages.rankLastAttackerA);
                    
                    victimCache.clearDamage();
                    event.setDeathMessage(null);
                    
                    return;
                }
            }
        }
        
        if (config.rankIPProtect) {
            String attackerIP = a.getAddress().getHostString();
            
            if (attackerIP != null && attackerIP.equalsIgnoreCase(v.getAddress().getHostString())) {
                v.sendMessage(messages.rankIPVictim);
                a.sendMessage(messages.rankIPAttacker);

                victimCache.clearDamage();
                event.setDeathMessage(null);
                
                return;
            }
        }

        int[] rankChanges = new int[2];
        int aP = attacker.getRank().getPoints();
        int vP = victim.getRank().getPoints();

        switch (config.rankSystem) {
            case PERCENT:
                Double d = victim.getRank().getPoints() * (config.percentRankChange / 100);
                rankChanges[0] = d.intValue();
                rankChanges[1] = d.intValue();
                break;
            case STATIC:
                rankChanges[0] = config.staticAttackerChange;
                rankChanges[1] = config.staticVictimChange;
                break;
            case ELO:
            default:
                rankChanges = getEloValues(vP, aP);
                break;
        }

        RankChangeEvent attackerEvent = new PointsChangeEvent(EventCause.USER, attacker.getRank(), attacker, rankChanges[0]);
        RankChangeEvent victimEvent = new PointsChangeEvent(EventCause.USER, victim.getRank(), attacker, rankChanges[1]);
        
        List<String> assistEntries = new ArrayList<>();
        List<User> messageReceivers = new ArrayList<>();
        
        if (SimpleEventHandler.handle(attackerEvent) && SimpleEventHandler.handle(victimEvent)) {
            double attackerDamage = victimCache.killedBy(attacker);
            
            if (config.assistEnable && victimCache.isAssisted()) {
                double toShare = attackerEvent.getChange() * (1 - config.assistKillerShare);
                double totalDamage = victimCache.getTotalDamage() + attackerDamage;
                int givenPoints = 0;

                Map<User, Double> damage = MapUtil.sortByValue(victimCache.getDamage());

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

                    if (!config.broadcastDeathMessage) {
                        messageReceivers.add(assist.getKey());
                    }
                    
                    givenPoints += addedPoints;
                    
                    String assistEntry = StringUtils.replace(messages.rankAssistEntry, "{PLAYER}", assist.getKey().getName());
                    assistEntry = StringUtils.replace(assistEntry, "{+}", Integer.toString(addedPoints));
                    assistEntry = StringUtils.replace(assistEntry, "{SHARE}", ChatUtils.getPercent(assistFraction));
                    assistEntries.add(assistEntry);
                    
                    assist.getKey().getRank().addPoints(addedPoints);
                }
                
                double attackerPoints = attackerEvent.getChange() - toShare + (givenPoints < toShare ? toShare - givenPoints : 0);
                attackerEvent.setChange((int) Math.round(attackerPoints));
            }
            
            attacker.getRank().addKill();
            attacker.getRank().addPoints(attackerEvent.getChange());
            attackerCache.setLastVictim(victim);

            victim.getRank().removePoints(victimEvent.getChange());
            victimCache.setLastAttacker(attacker);
            victimCache.clearDamage();
            
            if (!config.broadcastDeathMessage) {
                messageReceivers.add(attacker);
                messageReceivers.add(victim);
            }
        }

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

        if (config.dataType == DataType.MYSQL) {
            if (victim.hasGuild()) {
                taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(victim.getGuild()));
            }

            if (attacker.hasGuild()) {
                taskBuilder.delegate(new DatabaseUpdateGuildPointsRequest(attacker.getGuild()));
            }

            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(victim));
            taskBuilder.delegate(new DatabaseUpdateUserPointsRequest(attacker));
        }

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
        deathMessage = StringUtils.replace(deathMessage, "{POINTS}", Integer.toString(victim.getRank().getPoints()));
        deathMessage = StringUtils.replace(deathMessage, "{WEAPON}", MaterialUtils.getMaterialName(a.getItemInHand().getType()));
        deathMessage = StringUtils.replace(deathMessage, "{REMAINING-HEALTH}", String.format(Locale.US, "%.2f", a.getHealth()));
        deathMessage = StringUtils.replace(deathMessage, "{REMAINING-HEARTS}", Integer.toString((int) (a.getHealth() / 2)));

        if (victim.hasGuild()) {
            deathMessage = StringUtils.replace(deathMessage, "{VTAG}", StringUtils.replace(config.chatGuild, "{TAG}", victim.getGuild().getTag()));
        }

        if (attacker.hasGuild()) {
            deathMessage = StringUtils.replace(deathMessage, "{ATAG}", StringUtils.replace(config.chatGuild, "{TAG}", attacker.getGuild().getTag()));
        }

        deathMessage = StringUtils.replace(deathMessage, "{VTAG}", "");
        deathMessage = StringUtils.replace(deathMessage, "{ATAG}", "");
        
        if (config.assistEnable && !assistEntries.isEmpty()) {
            deathMessage += "\n" + StringUtils.replace(messages.rankAssistMessage, "{ASSISTS}", String.join(messages.rankAssistDelimiter, assistEntries));    
        }
        
        if (config.broadcastDeathMessage) {
            event.setDeathMessage(deathMessage);
        } else {
            event.setDeathMessage(null);
            
            for (User fighter : messageReceivers) {
                if (fighter.isOnline()) {
                    fighter.getPlayer().sendMessage(deathMessage);
                }
            }
        }
        
    }
	
    private int[] getEloValues(int vP, int aP) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        int[] rankChanges = new int[2];
        
        int aC = IntegerRange.inRange(aP, config.eloConstants, "ELO_CONSTANTS");
        int vC = IntegerRange.inRange(vP, config.eloConstants, "ELO_CONSTANTS");
        
        rankChanges[0] = (int) Math.round(aC * (1 - (1.0D / (1.0D + Math.pow(config.eloExponent, (vP - aP) / config.eloDivider)))));
        rankChanges[1] = (int) Math.round(vC * (0 - (1.0D / (1.0D + Math.pow(config.eloExponent, (aP - vP) / config.eloDivider)))) * -1);
        
        return rankChanges;
    }
	
}
