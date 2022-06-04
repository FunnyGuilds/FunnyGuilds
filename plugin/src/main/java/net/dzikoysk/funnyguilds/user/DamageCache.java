package net.dzikoysk.funnyguilds.user;

import java.time.Instant;

public class DamageCache {

    private final User attacker;

    private double damage;
    private Instant lastTime;

    public DamageCache(User attacker, double damage, Instant lastTime) {
        this.attacker = attacker;
        this.damage = damage;
        this.lastTime = lastTime;
    }

    public User getAttacker() {
        return this.attacker;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void addDamage(double damage) {
        this.damage += damage;
    }

    public Instant getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(Instant lastTime) {
        this.lastTime = lastTime;
    }

}
