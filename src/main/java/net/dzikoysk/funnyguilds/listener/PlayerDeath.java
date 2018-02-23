package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.RankChangeEvent;
import net.dzikoysk.funnyguilds.util.MaterialUtil;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.elo.EloUtils;
import net.dzikoysk.funnyguilds.util.hook.PluginHook;
import net.dzikoysk.funnyguilds.util.hook.WorldGuardHook;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
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

        if (PluginHook.isPresent(PluginHook.PLUGIN_WORLDGUARD)) {
            if (WorldGuardHook.isOnNonPointsRegion(v.getLocation()) || WorldGuardHook.isOnNonPointsRegion(a.getLocation())) {
                return;
            }
        }

        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();
        
        if (config.rankFarmingProtect) {
            if (attacker.getLastVictim() != null && attacker.getLastVictim().equals(victim)) {
                if (attacker.getLastVictimTime() + (config.rankFarmingCooldown * 1000) > System.currentTimeMillis()) {
                    v.sendMessage(messages.rankLastVictimV);
                    a.sendMessage(messages.rankLastVictimA);
                    event.setDeathMessage(null);
                    return;
                }
            } else if (victim.getLastAttacker() != null && victim.getLastAttacker().equals(attacker)) {
                if (victim.getLastVictimTime() + (config.rankFarmingCooldown * 1000) > System.currentTimeMillis()) {
                    v.sendMessage(messages.rankLastAttackerV);
                    a.sendMessage(messages.rankLastAttackerA);
                    event.setDeathMessage(null);
                    return;
                }
            }
        }

        int[] rankChanges = new int[2];

        switch (config.rankSystem) {
            case ELO:
                rankChanges = EloUtils.getRankChanges(attacker.getRank(), victim.getRank());
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
                rankChanges = EloUtils.getRankChanges(attacker.getRank(), victim.getRank());
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
                
                for (Entry<User, Double> assist : victim.getDamage().entrySet()) {
                    double assistFraction = assist.getValue() / totalDamage;
                    int addedPoints = (int) Math.round(assistFraction * toShare);

                    if (addedPoints <= 0) {
                        continue;
                    }

                    String assistEntry = StringUtils.replace(messages.rankAssistEntry, "{PLAYER}", assist.getKey().getName());
                    assistEntry = StringUtils.replace(assistEntry, "{+}", Integer.toString(addedPoints));
                    assistEntry = StringUtils.replace(assistEntry, "{SHARE}", StringUtils.getPercent(assistFraction));
                    assistEntries.add(assistEntry);
                    assist.getKey().getRank().addPoints(addedPoints);
                }
                
                attackerEvent.setChange((int) Math.round(attackerEvent.getChange() * config.assistKillerShare));
            }
            
            attacker.getRank().addKill();
            attacker.getRank().addPoints(attackerEvent.getChange());
            attacker.setLastVictim(victim);

            victim.getRank().removePoints(victimEvent.getChange());
            victim.setLastAttacker(attacker);
            victim.clearDamage();
        }
        
        if (config.dataType.mysql) {
            if (victim.hasGuild()) {
                IndependentThread.actions(ActionType.MYSQL_UPDATE_GUILD_POINTS, victim.getGuild());
            }

            if (attacker.hasGuild()) {
                IndependentThread.actions(ActionType.MYSQL_UPDATE_GUILD_POINTS, attacker.getGuild());
            }

            IndependentThread.actions(ActionType.MYSQL_UPDATE_USER_POINTS, victim);
            IndependentThread.actions(ActionType.MYSQL_UPDATE_USER_POINTS, attacker);
        }

        IndependentThread.actions(ActionType.DUMMY_GLOBAL_UPDATE_USER, victim);
        IndependentThread.actions(ActionType.DUMMY_GLOBAL_UPDATE_USER, attacker);
        IndependentThread.actions(ActionType.RANK_UPDATE_USER, victim);
        IndependentThread.action(ActionType.RANK_UPDATE_USER, attacker);

        String death = messages.rankDeathMessage;
        death = StringUtils.replace(death, "{ATTACKER}", attacker.getName());
        death = StringUtils.replace(death, "{VICTIM}", victim.getName());
        death = StringUtils.replace(death, "{+}", Integer.toString(attackerEvent.getChange()));
        death = StringUtils.replace(death, "{-}", Integer.toString(victimEvent.getChange()));
        death = StringUtils.replace(death, "{POINTS}", Integer.toString(victim.getRank().getPoints()));
        death = StringUtils.replace(death, "{WEAPON}", MaterialUtil.getMaterialName(a.getItemInHand().getType()));

        if (victim.hasGuild()) {
            death = StringUtils.replace(death, "{VTAG}", StringUtils.replace(config.chatGuild, "{TAG}", victim.getGuild().getTag()));
        }

        if (attacker.hasGuild()) {
            death = StringUtils.replace(death, "{ATAG}", StringUtils.replace(config.chatGuild, "{TAG}", attacker.getGuild().getTag()));
        }

        death = StringUtils.replace(death, "{VTAG}", "");
        death = StringUtils.replace(death, "{ATAG}", "");
        
        if (config.assistEnable && !assistEntries.isEmpty()) {
            death += "\n" + StringUtils.replace(messages.rankAssistMessage, "{ASSISTS}", String.join(messages.rankAssistDelimiter, assistEntries));
        }
        
        event.setDeathMessage(death);
    }
}