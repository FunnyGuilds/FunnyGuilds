package net.dzikoysk.funnyguilds.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.prefix.Dummy;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefix;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class UserCache {

    private final User user;

    private final Map<User, DamageCache> damageCaches = new HashMap<>();

    private DamageCache lastOldDamageCache;

    private final Cache<UUID, Instant> killerCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    private final Cache<UUID, Instant> victimCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    private IndividualPlayerList playerList;
    private Scoreboard scoreboard;
    private Option<IndividualPrefix> prefix = Option.none();
    private Dummy dummy;

    private BukkitTask teleportation;
    private long notificationTime;
    private boolean enter;
    private boolean spy;

    public UserCache(User user) {
        this.user = user;
    }

    public void addDamage(User user, double damage, Instant lastTime) {
        if (!this.damageCaches.containsKey(user)) {
            this.damageCaches.put(user, new DamageCache(user, damage, lastTime));
            return;
        }

        DamageCache damageCache = this.damageCaches.get(user);
        damageCache.addDamage(damage);
        damageCache.setLastTime(lastTime);
    }

    public double killedBy(User user) {
        if (user == null) {
            return 0.0D;
        }

        DamageCache damageCache = this.damageCaches.remove(user);
        if (damageCache == null) {
            return 0.0D;
        }

        return damageCache.getDamage();
    }

    public void clearDamage() {
        this.lastOldDamageCache = this.getLastAttackerDamageCache();
        this.damageCaches.clear();
    }

    public boolean toggleSpy() {
        this.spy = !this.spy;
        return this.spy;
    }

    public void setTeleportation(BukkitTask teleportation) {
        this.teleportation = teleportation;
    }

    public void setDummy(Dummy dummy) {
        this.dummy = dummy;
    }

    public void setIndividualPrefix(@Nullable IndividualPrefix prefix) {
        this.prefix = Option.of(prefix);
    }

    public void setEnter(boolean enter) {
        this.enter = enter;
    }

    public void setPlayerList(IndividualPlayerList playerList) {
        this.playerList = playerList;
    }

    public synchronized void setScoreboard(Scoreboard sb) {
        this.scoreboard = sb;
    }

    public void registerVictim(User user) {
        this.victimCache.put(user.getUUID(), Instant.now());
    }

    public void registerKiller(User user) {
        this.killerCache.put(user.getUUID(), Instant.now());
    }

    @Nullable
    public User getLastKiller() {
        return PandaStream.of(this.killerCache.asMap().entrySet())
                .sorted(Entry.<UUID, Instant>comparingByValue().reversed())
                .map(Entry::getKey)
                .head()
                .flatMap(FunnyGuilds.getInstance().getUserManager()::findByUuid)
                .orNull();
    }

    @Nullable
    public DamageCache getLastAttackerDamageCache() {
        DamageCache last = null;

        for (DamageCache damageCache : this.damageCaches.values()) {
            if (last == null) {
                last = damageCache;
                continue;
            }

            if (last.getLastTime().isBefore(damageCache.getLastTime())) {
                last = damageCache;
            }
        }

        if (last == null) {
            return this.lastOldDamageCache != null ? this.lastOldDamageCache : null;
        }

        return last;
    }

    @Nullable
    public Instant wasVictimOf(User attacker) {
        return this.killerCache.getIfPresent(attacker.getUUID());
    }

    @Nullable
    public Instant wasAttackerOf(User victim) {
        return this.victimCache.getIfPresent(victim.getUUID());
    }

    public void setNotificationTime(long notification) {
        this.notificationTime = notification;
    }

    public boolean isSpy() {
        return this.spy;
    }

    public BukkitTask getTeleportation() {
        return this.teleportation;
    }

    public Map<User, Double> getDamage() {
        Map<User, Double> damage = new HashMap<>();
        for (DamageCache damageCache : this.damageCaches.values()) {
            damage.put(damageCache.getAttacker(), damageCache.getDamage());
        }

        return damage;
    }

    public Double getTotalDamage() {
        return this.damageCaches.values().stream()
                .mapToDouble(DamageCache::getDamage)
                .sum();
    }

    public boolean isAssisted() {
        return !this.damageCaches.isEmpty();
    }

    public boolean isInCombat() {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        DamageCache damageCache = this.getLastAttackerDamageCache();

        if (damageCache == null || !damageCache.getAttacker().isOnline()) {
            return false;
        }

        return damageCache.getLastTime().plus(config.lastAttackerAsKillerConsiderationTimeout).compareTo(Instant.now()) >= 0;
    }

    public synchronized Option<Scoreboard> getScoreboard() {
        return Option.of(this.scoreboard);
    }

    public void updateScoreboardIfNull(Player player) {
        if (this.scoreboard != null) {
            return;
        }

        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        if (config.useSharedScoreboard) {
            this.setScoreboard(player.getScoreboard());
            return;
        }

        ScoreboardManager sbManager = Bukkit.getScoreboardManager();
        if (sbManager != null) {
            this.setScoreboard(sbManager.getNewScoreboard());
        }
    }

    public Option<IndividualPrefix> getIndividualPrefix() {
        return this.prefix;
    }

    public Dummy getDummy() {
        if (this.dummy == null) {
            this.dummy = new Dummy(this.user);
        }

        return this.dummy;
    }

    public long getNotificationTime() {
        return this.notificationTime;
    }

    public boolean getEnter() {
        return this.enter;
    }

    public Option<IndividualPlayerList> getPlayerList() {
        return Option.of(this.playerList);
    }

}
