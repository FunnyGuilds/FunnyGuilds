package net.dzikoysk.funnyguilds.basic.user;

import net.dzikoysk.funnyguilds.element.Dummy;
import net.dzikoysk.funnyguilds.element.IndividualPrefix;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class UserCache {

    private final User user;

    private final Map<User, Double> damage = new HashMap<>();
    private User lastAttacker;
    private User lastVictim;
    private long lastAttackerTime;
    private long lastVictimTime;

    private Scoreboard scoreboard;
    private IndividualPrefix prefix;
    private Dummy dummy;

    private BukkitTask teleportation;
    private long notificationTime;
    private boolean enter;
    //private boolean bypass;
    private boolean spy;

    public UserCache(User user) {
        this.user = user;
    }

    public void addDamage(User user, double damage) {
        Double currentDamage = this.damage.get(user);
        this.damage.put(user, (currentDamage == null ? 0.0D : currentDamage) + damage);
    }

    public double killedBy(User user) {
        if (user == null) {
            return 0.0D;
        }
        Double dmg = this.damage.remove(user);
        return dmg == null ? 0.0D : dmg;
    }

    public void clearDamage() {
        this.damage.clear();
    }

    public void setSpy(boolean spy) {
        this.spy = spy;
    }

    public void setTeleportation(BukkitTask teleportation) {
        this.teleportation = teleportation;
    }

    public void setDummy(Dummy dummy) {
        this.dummy = dummy;
    }

    public void setPrefix(IndividualPrefix prefix) {
        this.prefix = prefix;
    }

    public void setEnter(boolean enter) {
        this.enter = enter;
    }

    public synchronized void setScoreboard(Scoreboard sb) {
        this.scoreboard = sb;
    }

    public void setLastVictim(User user) {
        this.lastVictim = user;
        this.lastVictimTime = System.currentTimeMillis();
    }

    public void setLastAttacker(User user) {
        this.lastAttacker = user;
        this.lastAttackerTime = System.currentTimeMillis();
    }

    public void setNotificationTime(long notification) {
        this.notificationTime = notification;
    }

    public boolean isSpy() {
        return spy;
    }

    public BukkitTask getTeleportation() {
        return teleportation;
    }

    public Map<User, Double> getDamage() {
        return new HashMap<>(this.damage);
    }

    public Double getTotalDamage() {
        double dmg = 0.0D;
        for (double d : this.damage.values()) {
            dmg += d;
        }

        return dmg;
    }

    public boolean isAssisted() {
        return !this.damage.isEmpty();
    }

    public synchronized Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public IndividualPrefix getIndividualPrefix() {
        if (this.prefix == null) {
            new IndividualPrefix(user);
        }

        return this.prefix;
    }

    public Dummy getDummy() {
        if (this.dummy == null) {
            this.dummy = new Dummy(user);
        }

        return this.dummy;
    }

    public long getNotificationTime() {
        return this.notificationTime;
    }

    public boolean getEnter() {
        return this.enter;
    }

    public User getLastVictim() {
        return this.lastVictim;
    }

    public User getLastAttacker() {
        return this.lastAttacker;
    }

    public long getLastVictimTime() {
        return this.lastVictimTime;
    }

    public long getLastAttackerTime() {
        return this.lastAttackerTime;
    }

}
