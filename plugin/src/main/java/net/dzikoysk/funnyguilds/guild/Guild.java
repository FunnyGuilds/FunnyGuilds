package net.dzikoysk.funnyguilds.guild;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;
import java.util.Optional;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditEntry;
import net.dzikoysk.funnyguilds.guild.permission.GuildRolePolicy;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class Guild extends AbstractMutableEntity {

    private volatile GuildRolePolicy rolePolicy = GuildRolePolicy.EMPTY;
    private volatile List<GuildAuditEntry> auditEntries = List.of();

    public List<GuildAuditEntry> getAuditEntries() {
        return this.auditEntries;
    }

    public synchronized void setAuditEntries(List<GuildAuditEntry> entries) {
        this.auditEntries = List.copyOf(entries);
        this.markChanged();
    }

    private final UUID uuid;

    private volatile String name;
    private String tag;

    private final GuildRank rank;
    private int lives;
    private int heartLives;

    private Optional<Region> region = Optional.empty();
    private Optional<Location> home = Optional.empty();

    private User owner;
    private Set<User> members = ConcurrentHashMap.newKeySet();
    private Set<User> deputies = ConcurrentHashMap.newKeySet();
    private Set<Guild> allies = ConcurrentHashMap.newKeySet();
    private Set<Guild> enemies = ConcurrentHashMap.newKeySet();
    private final Set<UUID> alliedPvPGuilds = ConcurrentHashMap.newKeySet();

    private Instant born;
    private Instant validity;
    private Instant protection;
    private Optional<Instant> build = Optional.empty();
    private volatile Optional<Instant> ban = Optional.empty();
    private Optional<Instant> tntProtectionBypass = Optional.empty();

    private boolean pvp;

    private int warsWon;
    private int warsLost;
    private Optional<String> lastWarGuildTag = Optional.empty();
    private Optional<Instant> lastLifeBuy = Optional.empty();
    private int warStreak;
    private final java.util.Map<UUID, Instant> victimGainCooldowns = new ConcurrentHashMap<>();

    public Guild(UUID uuid, String name, String tag) {
        this.uuid = uuid != null ? uuid : UUID.randomUUID();
        this.name = name;
        this.tag = tag;

        this.rank = new GuildRank(this);
        this.born = Instant.now();
    }

    public Guild(String name, String tag) {
        this(null, name, tag);
    }

    public void broadcast(String message) {
        this.members.forEach(user -> user.sendMessage(message));
    }

    public void deserializationUpdate() {
        this.owner.setGuild(this);
        this.members.forEach(user -> user.setGuild(this));
    }

    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public synchronized void setName(String name) {
        this.name = name;
        this.markChanged();
    }

    public String getTag() {
        return this.tag;
    }

    public synchronized void setTag(String tag) {
        this.tag = tag;
        this.markChanged();
    }

    public GuildRank getRank() {
        return this.rank;
    }

    public int getLives() {
        return this.lives;
    }

    public synchronized void setLives(int lives) {
        this.lives = Math.max(0, lives);
        this.markChanged();
    }

    public synchronized void updateLives(IntFunction<Integer> update) {
        this.setLives(update.apply(this.lives));
    }

    public int getHeartLives() {
        return this.heartLives;
    }

    public synchronized void setHeartLives(int heartLives) {
        this.heartLives = Math.max(0, heartLives);
        this.markChanged();
    }

    public synchronized void updateHeartLives(IntFunction<Integer> update) {
        this.setHeartLives(update.apply(this.heartLives));
    }

    /**
     * @return region of the guild.
     */
    public Optional<Region> getRegion() {
        return this.region;
    }

    public boolean hasRegion() {
        return this.region.isPresent();
    }

    public synchronized void setRegion(@Nullable Region region) {
        this.region = Optional.ofNullable(region);
        this.region.ifPresent(peekRegion -> peekRegion.setGuild(this));
        this.markChanged();
    }

    public Optional<Location> getCenter() {
        return this.region.map(Region::getCenter);
    }

    public Optional<Location> getEnderCrystal() {
        return this.getCenter().map(location -> location.add(0.5D, -1.0D, 0.5D));
    }

    /**
     * @return home location of the guild
     */
    public Optional<Location> getHome() {
        return this.home.map(Location::clone);
    }

    public boolean hasHome() {
        return this.home.isPresent();
    }

    public synchronized void setHome(@Nullable Location home) {
        this.home = Optional.ofNullable(home).map(Location::clone);
        this.markChanged();
    }

    public void teleportHome(Player player) {
        this.getHome().ifPresent(player::teleport);
    }

    public User getOwner() {
        return this.owner;
    }

    public boolean isOwner(User user) {
        return this.owner.equals(user);
    }

    public synchronized void setOwner(User user) {
        if (this.owner != null && !this.owner.equals(user)) {
            this.rolePolicy = this.rolePolicy.removeMember(this.owner.getUUID()).removeMember(user.getUUID());
        }
        this.owner = user;
        this.addMember(user);
        this.markChanged();
    }

    public Set<User> getMembers() {
        return Collections.unmodifiableSet(this.members);
    }

    public Set<User> getOnlineMembers(boolean includeVanished) {
        return this.members.stream()
            .filter(User::isOnline)
            .filter(user -> includeVanished || !user.isVanished())
            .collect(Collectors.toSet());
    }

    public boolean isMember(User user) {
        return this.members.contains(user);
    }

    public synchronized void setMembers(Set<User> members) {
        this.members = copySet(members);
        this.markChanged();
    }

    public synchronized void addMember(User user) {
        this.members.add(user);
        this.markChanged();
    }

    public synchronized void removeMember(User user) {
        this.members.remove(user);
        this.deputies.remove(user);
        this.rolePolicy = this.rolePolicy.removeMember(user.getUUID());
        this.markChanged();
    }

    public Collection<User> getDeputies() {
        return Collections.unmodifiableSet(this.deputies);
    }

    public boolean hasDeputies() {
        return !this.deputies.isEmpty();
    }

    public boolean isDeputy(User user) {
        return this.deputies.contains(user);
    }

    public synchronized void setDeputies(Set<User> users) {
        this.deputies = copySet(users);
        this.markChanged();
    }

    public synchronized void addDeputy(User user) {
        if (this.deputies.add(user)) {
            this.rolePolicy = this.rolePolicy.removeMember(user.getUUID());
        }
        this.markChanged();
    }

    public synchronized void removeDeputy(User user) {
        if (this.deputies.remove(user)) {
            this.rolePolicy = this.rolePolicy.removeMember(user.getUUID());
        }
        this.markChanged();
    }

    public GuildRolePolicy getRolePolicy() {
        return this.rolePolicy;
    }

    public synchronized void setRolePolicy(GuildRolePolicy rolePolicy) {
        this.rolePolicy = rolePolicy;
        this.markChanged();
    }

    public Collection<Guild> getAllies() {
        return Collections.unmodifiableSet(this.allies);
    }

    public boolean hasAllies() {
        return !this.allies.isEmpty();
    }

    public boolean isAlly(@Nullable Guild guild) {
        if (guild == null) {
            return false;
        }
        return this.allies.contains(guild);
    }

    public synchronized void setAllies(Set<Guild> guilds) {
        this.allies = copySet(guilds);
        this.markChanged();
    }

    public synchronized void addAlly(Guild guild) {
        this.allies.add(guild);
        this.markChanged();
    }

    public synchronized void removeAlly(Guild guild) {
        this.allies.remove(guild);
        this.alliedPvPGuilds.remove(guild.uuid);
        this.markChanged();
    }

    public Collection<Guild> getEnemies() {
        return Collections.unmodifiableSet(this.enemies);
    }

    public boolean hasEnemies() {
        return !this.enemies.isEmpty();
    }

    public boolean isEnemy(@Nullable Guild guild) {
        if (guild == null) {
            return false;
        }
        return this.enemies.contains(guild);
    }

    public boolean isAtWar(@Nullable Guild guild) {
        if (guild == null) {
            return false;
        }
        return this.isEnemy(guild) || guild.isEnemy(this);
    }

    public synchronized void setEnemies(Set<Guild> guilds) {
        this.enemies = copySet(guilds);
        this.markChanged();
    }

    public synchronized void addEnemy(Guild guild) {
        this.enemies.add(guild);
        this.markChanged();
    }

    public synchronized void removeEnemy(Guild guild) {
        this.enemies.remove(guild);
        this.markChanged();
    }

    public boolean isNeutral(@Nullable Guild guild) {
        if (guild == null) {
            return true;
        }
        return !this.isEnemy(guild) && !this.isAlly(guild);
    }

    public Instant getBorn() {
        return this.born;
    }

    public synchronized void setBorn(Instant time) {
        this.born = time;
        this.markChanged();
    }

    public Instant getValidity() {
        return this.validity;
    }

    public boolean isValid() {
        return Instant.now().isBefore(this.validity);
    }

    public synchronized void setValidity(Instant time) {
        if (time == null || this.born.equals(time)) {
            time = Instant.now().plus(FunnyGuilds.getInstance().getPluginConfiguration().validityStart);
        }

        this.validity = time;
        this.markChanged();
    }

    public Instant getProtection() {
        return this.protection;
    }

    public boolean canBeAttacked() {
        return this.protection.isBefore(Instant.now());
    }

    public synchronized void setProtection(Instant protection) {
        this.protection = protection;
        this.markChanged();
    }

    public Optional<Instant> getBuild() {
        return this.build;
    }

    public synchronized boolean canBuild() {
        if (this.build.filter(build -> build.isAfter(Instant.now())).isPresent()) {
            return false;
        }

        this.build = Optional.empty();
        this.markChanged();
        return true;
    }

    public synchronized void setBuild(@Nullable Instant time) {
        if (time != null && time.isBefore(Instant.now())) {
            time = null;
        }

        this.build = Optional.ofNullable(time);
        this.markChanged();
    }

    public Optional<Instant> getBan() {
        return this.ban;
    }

    public boolean isBanned() {
        Instant expiresAt = this.ban.orElse(null);
        return expiresAt != null && expiresAt.isAfter(Instant.now());
    }

    public synchronized void setBan(@Nullable Instant time) {
        Optional<Instant> newBan = Optional.ofNullable(time);
        if (!this.ban.equals(newBan)) {
            this.ban = newBan;
            this.markChanged();
        }
    }

    public synchronized void expireBan(Instant now) {
        Instant expiresAt = this.ban.orElse(null);
        if (expiresAt != null && !expiresAt.isAfter(now)) {
            this.setBan(null);
        }
    }

    public Optional<Instant> getTntProtectionBypass() {
        return this.tntProtectionBypass;
    }

    public boolean hasTntProtectionBypass() {
        return this.tntProtectionBypass.filter(bypass -> bypass.isAfter(Instant.now())).isPresent();
    }

    public synchronized void setTntProtectionBypass(@Nullable Instant time) {
        if (time != null && time.isBefore(Instant.now())) {
            time = null;
        }

        this.tntProtectionBypass = Optional.ofNullable(time);
        this.markChanged();
    }

    public boolean hasPvPEnabled() {
        return this.pvp;
    }

    public synchronized void setPvP(boolean pvp) {
        this.pvp = pvp;
        this.markChanged();
    }

    public synchronized boolean togglePvP() {
        this.pvp = !this.pvp;
        this.markChanged();
        return this.pvp;
    }

    public int getWarsWon() {
        return this.warsWon;
    }

    public synchronized void setWarsWon(int warsWon) {
        this.warsWon = Math.max(0, warsWon);
        this.markChanged();
    }

    public synchronized void updateWarsWon(IntFunction<Integer> update) {
        this.setWarsWon(update.apply(this.warsWon));
    }

    public int getWarsLost() {
        return this.warsLost;
    }

    public synchronized void setWarsLost(int warsLost) {
        this.warsLost = Math.max(0, warsLost);
        this.markChanged();
    }

    public synchronized void updateWarsLost(IntFunction<Integer> update) {
        this.setWarsLost(update.apply(this.warsLost));
    }

    public Optional<String> getLastWarGuildTag() {
        return this.lastWarGuildTag;
    }

    public synchronized void setLastWarGuildTag(@Nullable String lastWarGuildTag) {
        this.lastWarGuildTag = Optional.ofNullable(lastWarGuildTag);
        this.markChanged();
    }

    public int getWarStreak() {
        return this.warStreak;
    }

    public synchronized void setWarStreak(int warStreak) {
        this.warStreak = Math.max(0, warStreak);
        this.markChanged();
    }

    public synchronized void updateWarStreak(IntFunction<Integer> update) {
        this.setWarStreak(update.apply(this.warStreak));
    }

    public Optional<Instant> getLastLifeBuy() {
        return this.lastLifeBuy;
    }

    public synchronized void setLastLifeBuy(@Nullable Instant lastLifeBuy) {
        this.lastLifeBuy = Optional.ofNullable(lastLifeBuy);
        this.markChanged();
    }

    public boolean canGainLifeOn(Guild victimGuild, java.time.Duration cooldown) {
        if (cooldown == null || cooldown.isZero() || cooldown.isNegative()) {
            return true;
        }
        Instant until = this.victimGainCooldowns.get(victimGuild.getUUID());
        return until == null || Instant.now().isAfter(until);
    }

    public void setGainLifeCooldownOn(Guild victimGuild, java.time.Duration cooldown) {
        if (cooldown != null && !cooldown.isZero() && !cooldown.isNegative()) {
            this.victimGainCooldowns.put(victimGuild.getUUID(), Instant.now().plus(cooldown));
        }
    }

    public boolean hasAllyPvPEnabled(Guild alliedGuild) {
        return this.allies.contains(alliedGuild) && this.alliedPvPGuilds.contains(alliedGuild.uuid);
    }

    public Set<UUID> getAlliedPvPGuilds() {
        return Set.copyOf(this.alliedPvPGuilds);
    }

    public synchronized void setAlliedPvPGuilds(Set<UUID> guilds) {
        this.alliedPvPGuilds.clear();
        this.alliedPvPGuilds.addAll(guilds);
        this.markChanged();
    }

    public synchronized boolean toggleAllyPvP(Guild alliedGuild) {
        boolean enabled = false;

        if (!this.alliedPvPGuilds.remove(alliedGuild.uuid)) {
            this.alliedPvPGuilds.add(alliedGuild.uuid);
            enabled = true;
        }

        this.markChanged();
        return enabled;
    }

    private static <T> Set<T> copySet(Set<T> values) {
        Set<T> copy = ConcurrentHashMap.newKeySet();
        copy.addAll(values);
        return copy;
    }

    @Override
    public EntityType getType() {
        return EntityType.GUILD;
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        Guild guild = (Guild) obj;
        return this.uuid.equals(guild.uuid);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
