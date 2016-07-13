package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private static long attackerCooldown = 7200000L;
    private static long victimCooldown = 7200000L;

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
        attacker.getRank().addKill();

        if (attacker.getLastVictim() != null && attacker.getLastVictim().equals(victim)) {
            if (attacker.getLastVictimTime() + attackerCooldown > System.currentTimeMillis()) {
                v.sendMessage(Messages.getInstance().getMessage("rankLastVictimV"));
                a.sendMessage(Messages.getInstance().getMessage("rankLastVictimA"));
                changeMessage(event, attacker, victim, 0, 0);
                return;
            }
        }
        else if (victim.getLastAttacker() != null && victim.getLastAttacker().equals(attacker)) {
            if (victim.getLastVictimTime() + victimCooldown > System.currentTimeMillis()) {
                v.sendMessage(Messages.getInstance().getMessage("rankLastAttackerV"));
                a.sendMessage(Messages.getInstance().getMessage("rankLastAttackerA"));
                changeMessage(event, attacker, victim, 0, 0);
                return;
            }
        }

        int wOldPoints = attacker.getRank().getPoints();
        int wKFactor = getKFactor(wOldPoints);

        int lOldPoints = victim.getRank().getPoints();
        int lKFactor = getKFactor(lOldPoints);

        double w = Math.pow(10, wOldPoints / 400);
        double l = Math.pow(10, lOldPoints / 400);

        double ew = w / (w + l);
        double el = l / (w + l);

        int ws = 1;
        int ls = 0;

        double wNewPoints = wOldPoints + wKFactor * (ws - ew);
        double lNewPoints = lOldPoints + wKFactor * (ls - el);

        attacker.getRank().setPoints((int) wNewPoints);
        victim.getRank().setPoints((int) lNewPoints);

        int wDiff = (int) (wOldPoints - wNewPoints);
        int lDiff = (int) (lOldPoints - lNewPoints);

        if (Settings.getInstance().mysql) {
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

        changeMessage(event, attacker, victim, wDiff, lDiff);
    }

    private int getKFactor(int wOldPoints) {
        int wKFactor = 30;
        if (wOldPoints < 2000) {
            wKFactor = 30;
        } else if (wOldPoints >= 2000 && wOldPoints <= 2400) {
            wKFactor = 130 - ((wOldPoints) / 20);
        } else if (wOldPoints > 2400) {
            wKFactor = 10;
        }
        return wKFactor;
    }

    private void changeMessage(PlayerDeathEvent event, User attacker, User victim, int pointsA, int pointsV) {
        String death = Messages.getInstance().getMessage("rankDeathMessage");
        death = StringUtils.replace(death, "{ATTACKER}", attacker.getName());
        death = StringUtils.replace(death, "{VICTIM}", victim.getName());
        death = StringUtils.replace(death, "{+}", Integer.toString(pointsA));
        death = StringUtils.replace(death, "{-}", Integer.toString(pointsV));
        death = StringUtils.replace(death, "{POINTS}", Integer.toString(victim.getRank().getPoints()));
        if (victim.hasGuild()) {
            death = StringUtils.replace(death, "{VTAG}",
                    StringUtils.replace(Settings.getInstance().chatGuild, "{TAG}", victim.getGuild().getTag()));
        }
        if (attacker.hasGuild()) {
            death = StringUtils.replace(death, "{ATAG}",
                    StringUtils.replace(Settings.getInstance().chatGuild, "{TAG}", attacker.getGuild().getTag()));
        }
        death = StringUtils.replace(death, "{VTAG}", "");
        death = StringUtils.replace(death, "{ATAG}", "");
        event.setDeathMessage(death);
    }

    public static long getAttackerCooldown() {
        return victimCooldown;
    }

    public static long getVictimCooldown() {
        return victimCooldown;
    }

}
