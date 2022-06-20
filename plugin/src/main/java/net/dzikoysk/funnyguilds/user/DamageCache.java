package net.dzikoysk.funnyguilds.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration.DamageTracking;
import net.dzikoysk.funnyguilds.shared.MapUtils;
import org.jetbrains.annotations.Nullable;

public class DamageCache {

    private final User user;

    private final LinkedList<Damage> damageHistory = new LinkedList<>();
    private final Cache<UUID, Instant> killHistory = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public DamageCache(User user) {
        this.user = user;
    }

    public Map<User, Double> getTotalDamageMap() {
        Map<User, Double> damageMap = new HashMap<>();
        for (Damage damage : this.damageHistory) {
            double damageAmount = damageMap.getOrDefault(damage.getAttacker(), 0.0);
            damageAmount += damage.getDamage();
            damageMap.put(damage.getAttacker(), damageAmount);
        }
        return damageMap;
    }

    public Map<User, Double> getSortedTotalDamageMap() {
        return MapUtils.sortByValue(this.getTotalDamageMap());
    }

    public double getTotalDamage() {
        return this.damageHistory
                .stream()
                .mapToDouble(Damage::getDamage)
                .sum();
    }

    public double getTotalDamage(User user) {
        return this.getTotalDamageMap().getOrDefault(user, 0.0);
    }

    @Nullable
    public Damage getLastDamage() {
        if (this.damageHistory.isEmpty()) {
            return null;
        }
        return this.damageHistory.getLast();
    }

    public boolean isInCombat() {
        Damage lastDamage = this.getLastDamage();
        if (lastDamage == null) {
            return false;
        }

        if (!lastDamage.getAttacker().isOnline()) {
            return false;
        }

        Duration lastAttackerAsKillerConsiderationTimeout = FunnyGuilds.getInstance().getPluginConfiguration().lastAttackerAsKillerConsiderationTimeout;
        return lastDamage.isExpired(lastAttackerAsKillerConsiderationTimeout);
    }

    @Nullable
    public Instant getLastKillTime(User user) {
        return this.killHistory.getIfPresent(user.getUUID());
    }

    public void addDamage(User damageDealer, double damage) {
        // Prevent players from damaging themselves to for eg. avoid points loss after logout
        if (this.user.equals(damageDealer)) {
            return;
        }

        this.damageHistory.add(new Damage(damageDealer, damage));
        this.update();
    }

    public void addKill(User killer) {
        this.killHistory.put(killer.getUUID(), Instant.now());
    }

    public void update() {
        if (this.damageHistory.isEmpty()) {
            return;
        }

        DamageTracking trackingConfig = FunnyGuilds.getInstance().getPluginConfiguration().damageTracking;

        // Remove expired
        this.damageHistory.removeIf(damage -> this.damageHistory.size() > 1 && damage.isExpired(trackingConfig.expireTime));

        // Remove over limit
        while (this.damageHistory.size() > trackingConfig.maxTracks) {
            this.damageHistory.remove();
        }
    }

    public void clear() {
        this.damageHistory.clear();
    }

    public static class Damage {

        private final User attacker;
        private final double damage;
        private final Instant attackTime;

        private Damage(User attacker, double damage, Instant attackTime) {
            this.attacker = attacker;
            this.damage = damage;
            this.attackTime = attackTime;
        }

        private Damage(User attacker, double damage) {
            this(attacker, damage, Instant.now());
        }

        public User getAttacker() {
            return this.attacker;
        }

        public double getDamage() {
            return this.damage;
        }

        public Instant getAttackTime() {
            return this.attackTime;
        }

        public boolean isExpired(Duration expireTime) {
            return Duration.between(this.attackTime, Instant.now()).compareTo(expireTime) > 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.attacker, this.damage, this.attackTime);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Damage)) {
                return false;
            }
            Damage dmg = (Damage) obj;
            return Double.compare(dmg.damage, this.damage) == 0 && this.attacker.equals(dmg.attacker) && this.attackTime.equals(dmg.attackTime);
        }

    }

}
