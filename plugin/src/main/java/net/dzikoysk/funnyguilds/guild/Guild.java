package net.dzikoysk.funnyguilds.guild;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;
import java.util.Optional;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.IdentifiableEntity;
import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class Guild extends AbstractMutableEntity implements IdentifiableEntity {

    private final UUID uuid;

    private String name;
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
    private Optional<Instant> ban = Optional.empty();
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

    public void setName(String name) {
        this.name = name;
        this.markChanged();
        FunnyGuilds.getInstance().getRankService().refreshGuild(this);
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.markChanged();
    }

    public GuildRank getRank() {
        return this.rank;
    }

    public int getLives() {
        return this.lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, lives);
        this.markChanged();
    }

    public void updateLives(IntFunction<Integer> update) {
        this.setLives(update.apply(this.lives));
    }

    public int getHeartLives() {
        return this.heartLives;
    }

    public void setHeartLives(int heartLives) {
        this.heartLives = Math.max(0, heartLives);
        this.markChanged();
    }

    public void updateHeartLives(IntFunction<Integer> update) {
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

    public void setRegion(@Nullable Region region) {
        this.region = Optional.ofNullable(region);
        this.region.ifPresent(peekRegion -> peekRegion.setGuild(this));
        this.markChanged();
    }

    public Optional<Location> getCenter() {
        return this.region
                .map(Region::getCenter)
                .map(Location::clone);
    }

    public Optional<Location> getEnderCrystal() {
        return this.getCenter().map(location -> location.add(0.5D, -1.0D, 0.5D));
    }

    /**
     * @return home location of the guild
     */
    public Optional<Location> getHome() {
        return this.home;
    }

    public boolean hasHome() {
        return this.home.isPresent();
    }

    public void setHome(@Nullable Location home) {
        this.home = Optional.ofNullable(home);
        this.markChanged();
    }

    public void teleportHome(Player player) {
        this.home.ifPresent(player::teleport);
    }

    public User getOwner() {
        return this.owner;
    }

    public boolean isOwner(User user) {
        return this.owner.equals(user);
    }

    public void setOwner(User user) {
        this.owner = user;
        this.addMember(user);
        this.markChanged();
    }

    public Set<User> getMembers() {
        return this.members;
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

    public void setMembers(Set<User> members) {
        Set<User> previous = this.members;
        this.members = ConcurrentHashMap.newKeySet();
        this.members.addAll(members);
        if (FunnyGuilds.getInstance().getGuildManager().findByUuid(this.uuid).orElse(null) == this) {
            previous.stream().filter(user -> !this.members.contains(user)).forEach(user -> {
                if (user.getGuild().orElse(null) == this) {
                    user.removeGuild();
                }
            });
            this.members.forEach(user -> user.setGuild(this));
        }
        this.markChanged();
        FunnyGuilds.getInstance().getRankService().refreshGuild(this);
    }

    public void addMember(User user) {
        boolean added = this.members.add(user);
        if (FunnyGuilds.getInstance().getGuildManager().findByUuid(this.uuid).orElse(null) == this
                && user.getGuild().orElse(null) != this) {
            user.setGuild(this);
        }
        if (!added) {
            return;
        }
        this.markChanged();
        FunnyGuilds.getInstance().getRankService().refreshGuild(this);
    }

    public void removeMember(User user) {
        boolean removed = this.members.remove(user);
        this.deputies.remove(user);
        if (user.getGuild().orElse(null) == this) {
            user.removeGuild();
        }
        if (!removed) {
            return;
        }
        this.markChanged();
        FunnyGuilds.getInstance().getRankService().refreshGuild(this);
    }

    public Collection<User> getDeputies() {
        return this.deputies;
    }

    public boolean hasDeputies() {
        return !this.deputies.isEmpty();
    }

    public boolean isDeputy(User user) {
        return this.deputies.contains(user);
    }

    public void setDeputies(Set<User> users) {
        this.deputies = users;
        this.markChanged();
    }

    public void addDeputy(User user) {
        this.deputies.add(user);
        this.markChanged();
    }

    public void removeDeputy(User user) {
        this.deputies.remove(user);
        this.markChanged();
    }

    public Collection<Guild> getAllies() {
        return this.allies;
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

    public void setAllies(Set<Guild> guilds) {
        this.allies = guilds;
        this.markChanged();
    }

    public void addAlly(Guild guild) {
        this.allies.add(guild);
        this.markChanged();
    }

    public void removeAlly(Guild guild) {
        this.allies.remove(guild);
        this.markChanged();
    }

    public Collection<Guild> getEnemies() {
        return this.enemies;
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

    public void setEnemies(Set<Guild> guilds) {
        this.enemies = guilds;
        this.markChanged();
    }

    public void addEnemy(Guild guild) {
        this.enemies.add(guild);
        this.markChanged();
    }

    public void removeEnemy(Guild guild) {
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

    public void setBorn(Instant time) {
        this.born = time;
        this.markChanged();
    }

    public Instant getValidity() {
        return this.validity;
    }

    public boolean isValid() {
        return Instant.now().isBefore(this.validity);
    }

    public void setValidity(Instant time) {
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

    public void setProtection(Instant protection) {
        this.protection = protection;
        this.markChanged();
    }

    public Optional<Instant> getBuild() {
        return this.build;
    }

    public boolean canBuild() {
        if (this.build.filter(build -> build.isAfter(Instant.now())).isPresent()) {
            return false;
        }

        this.build = Optional.empty();
        this.markChanged();
        return true;
    }

    public void setBuild(@Nullable Instant time) {
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
        if (this.ban.filter(ban -> ban.isAfter(Instant.now())).isPresent()) {
            return true;
        }

        this.ban = Optional.empty();
        this.markChanged();
        return false;
    }

    public void setBan(@Nullable Instant time) {
        if (time != null && time.isBefore(Instant.now())) {
            time = null;
        }

        this.ban = Optional.ofNullable(time);
        this.markChanged();
    }

    public Optional<Instant> getTntProtectionBypass() {
        return this.tntProtectionBypass;
    }

    public boolean hasTntProtectionBypass() {
        return this.tntProtectionBypass.filter(bypass -> bypass.isAfter(Instant.now())).isPresent();
    }

    public void setTntProtectionBypass(@Nullable Instant time) {
        if (time != null && time.isBefore(Instant.now())) {
            time = null;
        }

        this.tntProtectionBypass = Optional.ofNullable(time);
        this.markChanged();
    }

    public boolean hasPvPEnabled() {
        return this.pvp;
    }

    public void setPvP(boolean pvp) {
        this.pvp = pvp;
        this.markChanged();
    }

    public boolean togglePvP() {
        this.pvp = !this.pvp;
        this.markChanged();
        return this.pvp;
    }

    public int getWarsWon() {
        return this.warsWon;
    }

    public void setWarsWon(int warsWon) {
        this.warsWon = Math.max(0, warsWon);
        this.markChanged();
        FunnyGuilds.getInstance().getRankService().refreshGuild(this);
    }

    public void updateWarsWon(IntFunction<Integer> update) {
        this.setWarsWon(update.apply(this.warsWon));
    }

    public int getWarsLost() {
        return this.warsLost;
    }

    public void setWarsLost(int warsLost) {
        this.warsLost = Math.max(0, warsLost);
        this.markChanged();
        FunnyGuilds.getInstance().getRankService().refreshGuild(this);
    }

    public void updateWarsLost(IntFunction<Integer> update) {
        this.setWarsLost(update.apply(this.warsLost));
    }

    public Optional<String> getLastWarGuildTag() {
        return this.lastWarGuildTag;
    }

    public void setLastWarGuildTag(@Nullable String lastWarGuildTag) {
        this.lastWarGuildTag = Optional.ofNullable(lastWarGuildTag);
        this.markChanged();
    }

    public int getWarStreak() {
        return this.warStreak;
    }

    public void setWarStreak(int warStreak) {
        this.warStreak = Math.max(0, warStreak);
        this.markChanged();
        FunnyGuilds.getInstance().getRankService().refreshGuild(this);
    }

    public void updateWarStreak(IntFunction<Integer> update) {
        this.setWarStreak(update.apply(this.warStreak));
    }

    public Optional<Instant> getLastLifeBuy() {
        return this.lastLifeBuy;
    }

    public void setLastLifeBuy(@Nullable Instant lastLifeBuy) {
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

    public boolean toggleAllyPvP(Guild alliedGuild) {
        boolean enabled = false;

        if (!this.alliedPvPGuilds.remove(alliedGuild.uuid)) {
            this.alliedPvPGuilds.add(alliedGuild.uuid);
            enabled = true;
        }

        this.markChanged();
        return enabled;
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
