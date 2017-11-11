package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.util.MaterialUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.elo.EloUtils;
import net.dzikoysk.funnyguilds.util.hook.PluginHook;
import net.dzikoysk.funnyguilds.util.hook.WorldGuardHook;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

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

        if (Settings.getConfig().rankFarmingProtect) {
            if (attacker.getLastVictim() != null && attacker.getLastVictim().equals(victim)) {
                if (attacker.getLastVictimTime() + (Settings.getConfig().rankFarmingCooldown * 1000) > System.currentTimeMillis()) {
                    v.sendMessage(Messages.getInstance().rankLastVictimV);
                    a.sendMessage(Messages.getInstance().rankLastVictimA);
                    event.setDeathMessage(null);
                    return;
                }
            } else if (victim.getLastAttacker() != null && victim.getLastAttacker().equals(attacker)) {
                if (victim.getLastVictimTime() + (Settings.getConfig().rankFarmingCooldown * 1000) > System.currentTimeMillis()) {
                    v.sendMessage(Messages.getInstance().rankLastAttackerV);
                    a.sendMessage(Messages.getInstance().rankLastAttackerA);
                    event.setDeathMessage(null);
                    return;
                }
            }
        }

        int[] rankChanges = new int[2];
        
        switch(Settings.getConfig().rankSystem) {
        case ELO:
            rankChanges = EloUtils.getRankChanges(attacker.getRank(), victim.getRank());
            break;
        case PERCENT:
            Double d = victim.getRank().getPoints() * (Settings.getConfig().percentRankChange / 100);
            rankChanges[0] = d.intValue();
            rankChanges[1] = d.intValue();
            break;
        case STATIC:
            rankChanges[0] = Settings.getConfig().staticAttackerChange;
            rankChanges[1] = Settings.getConfig().staticVictimChange;
            break;
        default:
            rankChanges = EloUtils.getRankChanges(attacker.getRank(), victim.getRank());
            break;
        }

        attacker.getRank().addKill();
        attacker.getRank().addPoints(rankChanges[0]);
        attacker.setLastVictim(victim);
        
        victim.getRank().removePoints(rankChanges[1]);
        victim.setLastAttacker(attacker);

        if (Settings.getConfig().dataType.mysql) {
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

        String death = Messages.getInstance().rankDeathMessage;
        death = StringUtils.replace(death, "{ATTACKER}", attacker.getName());
        death = StringUtils.replace(death, "{VICTIM}", victim.getName());
        death = StringUtils.replace(death, "{+}", Integer.toString(rankChanges[0]));
        death = StringUtils.replace(death, "{-}", Integer.toString(rankChanges[1]));
        death = StringUtils.replace(death, "{POINTS}", Integer.toString(victim.getRank().getPoints()));
        death = StringUtils.replace(death, "{WEAPON}", MaterialUtil.getMaterialName(a.getItemInHand().getType()));
        
        if (victim.hasGuild()) {
            death = StringUtils.replace(death, "{VTAG}", StringUtils.replace(Settings.getConfig().chatGuild, "{TAG}", victim.getGuild().getTag()));
        }
        
        if (attacker.hasGuild()) {
            death = StringUtils.replace(death, "{ATAG}", StringUtils.replace(Settings.getConfig().chatGuild, "{TAG}", attacker.getGuild().getTag()));
        }
        
        death = StringUtils.replace(death, "{VTAG}", "");
        death = StringUtils.replace(death, "{ATAG}", "");
        event.setDeathMessage(death);
    }
}