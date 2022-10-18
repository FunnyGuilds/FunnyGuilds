package net.dzikoysk.funnyguilds.damage;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration.DamageTracking;
import net.dzikoysk.funnyguilds.shared.MapUtils;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public class DamageState {

    private final UUID ownerUUID;

    private final LinkedList<Damage> damageHistory = new LinkedList<>();
    private final Map<UUID, Instant> killHistory = new HashMap<>();

    public DamageState(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    private Map<User, Double> getTotalDamageMap() {
        Map<User, Double> damageMap = new HashMap<>();

        for (Damage damage : this.damageHistory) {
            double damageAmount = damageMap.getOrDefault(damage.getAttacker(), 0.0);
            damageAmount += damage.getDamage();
            damageMap.put(damage.getAttacker(), damageAmount);
        }

        return damageMap;
    }

    public Map<User, Double> getSortedTotalDamageMap() {
        return Collections.unmodifiableMap(MapUtils.sortByValue(this.getTotalDamageMap()));
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

    public Option<Damage> getLastDamage() {
        if (this.damageHistory.isEmpty()) {
            return Option.none();
        }
        return Option.of(this.damageHistory.getLast());
    }

    public boolean isInCombat() {
        Option<Damage> lastDamageOption = this.getLastDamage();
        if (lastDamageOption.isEmpty()) {
            return false;
        }
        Damage lastDamage = lastDamageOption.get();

        if (!lastDamage.getAttacker().isOnline()) {
            return false;
        }

        Duration lastAttackerAsKillerConsiderationTimeout = FunnyGuilds.getInstance().getPluginConfiguration().lastAttackerAsKillerConsiderationTimeout;
        return !lastDamage.isExpired(lastAttackerAsKillerConsiderationTimeout);
    }

    public Option<Instant> getLastKillTime(User user) {
        return Option.of(this.killHistory.get(user.getUUID()));
    }

    public void addDamage(User damageDealer, double damage) {
        // Prevent players from damaging themselves to for eg. avoid points loss after logout
        if (this.ownerUUID.equals(damageDealer.getUUID())) {
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
        if (trackingConfig.maxTracks < 1) {
            return;
        }

        while (this.damageHistory.size() > trackingConfig.maxTracks) {
            this.damageHistory.remove();
        }
    }

    public void clear() {
        this.damageHistory.clear();
    }

}
