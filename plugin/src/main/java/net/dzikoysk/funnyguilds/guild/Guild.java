package net.dzikoysk.funnyguilds.guild;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.rank.RankManager;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class Guild extends AbstractMutableEntity {

    private final UUID uuid;

    private String name;
    private String tag;

    private final GuildRank rank;
    private int lives;

    private Option<Region> region = Option.none();
    private Option<Location> home = Option.none();

    private User owner;
    private Set<User> members = ConcurrentHashMap.newKeySet();
    private Set<User> deputies = ConcurrentHashMap.newKeySet();
    private Set<Guild> allies = ConcurrentHashMap.newKeySet();
    private Set<Guild> enemies = ConcurrentHashMap.newKeySet();
    private Set<UUID> alliedFFGuilds = ConcurrentHashMap.newKeySet();

    private long born;
    private long validity;
    private Date validityDate;
    private long protection;
    private long build;
    private long ban;

    private boolean pvp;

    public Guild(UUID uuid) {
        this.uuid = uuid;

        this.rank = new GuildRank(this);
        this.born = System.currentTimeMillis();
    }

    public Guild(@NotNull String name) {
        this(UUID.randomUUID());
        this.name = name;
    }

    public Guild(@NotNull String name, @NotNull String tag) {
        this(UUID.randomUUID());
        this.name = name;
        this.tag = tag;
    }

    public Guild(UUID uuid, @NotNull String name, @NotNull String tag) {
        this(uuid != null ? uuid : UUID.randomUUID());
        this.name = name;
        this.tag = tag;
    }

    public void broadcast(String message) {
        this.getOnlineMembers().forEach(user -> user.sendMessage(message));
    }

    public void deserializationUpdate() {
        owner.setGuild(this);
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
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.markChanged();
    }

    @NotNull
    public GuildRank getRank() {
        return this.rank;
    }

    /**
     * @return true if guild is ranked; false if guild is not ranked.
     * @deprecated for removal in the future, in favour of {@link RankManager#isRankedGuild(Guild)}}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "4.11.0")
    public boolean isRanked() {
        return RankManager.getInstance().isRankedGuild(this);
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

    /**
     * @return region of the guild.
     */
    @NotNull
    public Option<Region> getRegion() {
        return this.region;
    }

    public boolean hasRegion() {
        return this.region.isPresent();
    }

    public void setRegion(@Nullable Region region) {
        this.region = Option.of(region);

        this.region.peek(peekRegion -> {
            peekRegion.setGuild(this);
            peekRegion.getCenter()
                    .peek(center -> this.home = Option.of(center.clone()));
        });

        this.markChanged();
    }

    @NotNull
    public Option<Location> getCenter() {
        return this.region
                .flatMap(Region::getCenter)
                .map(Location::clone);
    }

    public Option<Location> getEnderCrystal() {
        return this.getCenter().map(location -> location.add(0.5D, -1.0D, 0.5D));
    }

    /**
     * @return if someone is in guild region
     * @deprecated for removal in the future, in favour of {@link RegionManager#isAnyUserInRegion(Region, Collection)}}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "4.11.0")
    public boolean isSomeoneInRegion() {
        return FunnyGuilds.getInstance().getRegionManager().isAnyUserInRegion(region, new HashSet<>(members));
    }

    /**
     * @return home location of the guild
     */
    @NotNull
    public Option<Location> getHome() {
        return this.home;
    }

    public boolean hasHome() {
        return this.home.isPresent();
    }

    public void setHome(@Nullable Location home) {
        this.home = Option.of(home);
        this.markChanged();
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(@NotNull User user) {
        this.owner = user;
        this.addMember(user);
        this.markChanged();
    }

    public Set<User> getMembers() {
        return this.members;
    }

    public Set<User> getOnlineMembers() {
        return this.members
                .stream()
                .filter(User::isOnline)
                .collect(Collectors.toSet());
    }

    public void setMembers(Set<User> members) {
        this.members = Collections.synchronizedSet(members);
        this.markChanged();
    }

    public void addMember(User user) {
        if (this.members.contains(user)) {
            return;
        }

        this.members.add(user);
        this.markChanged();
    }

    public void removeMember(User user) {
        this.deputies.remove(user);
        this.members.remove(user);
        this.markChanged();
    }

    public Set<User> getDeputies() {
        return this.deputies;
    }

    public boolean hasDeputies() {
        return !this.deputies.isEmpty();
    }

    public void setDeputies(Set<User> users) {
        this.deputies = users;
        this.markChanged();
    }

    public void addDeputy(User user) {
        if (this.deputies.contains(user)) {
            return;
        }

        this.deputies.add(user);
        this.markChanged();
    }

    public void removeDeputy(User user) {
        this.deputies.remove(user);
        this.markChanged();
    }

    public Set<Guild> getAllies() {
        return this.allies;
    }

    public boolean hasAllies() {
        return !this.allies.isEmpty();
    }

    public void setAllies(Set<Guild> guilds) {
        this.allies = guilds;
        this.markChanged();
    }

    public void addAlly(Guild guild) {
        this.markChanged();
        if (this.allies.contains(guild)) {
            return;
        }

        this.allies.add(guild);
    }

    public void removeAlly(Guild guild) {
        this.allies.remove(guild);
        this.markChanged();
    }

    public Set<Guild> getEnemies() {
        return enemies;
    }

    public boolean hasEnemies() {
        return !this.enemies.isEmpty();
    }

    public void setEnemies(Set<Guild> guilds) {
        this.enemies = guilds;
        this.markChanged();
    }

    public void addEnemy(Guild guild) {
        if (this.enemies.contains(guild)) {
            return;
        }

        this.enemies.add(guild);
        this.markChanged();
    }

    public void removeEnemy(Guild guild) {
        this.enemies.remove(guild);
        this.markChanged();
    }

    public long getBorn() {
        return this.born;
    }

    public void setBorn(long time) {
        this.born = time;
        this.markChanged();
    }

    public long getValidity() {
        return this.validity;
    }

    public Date getValidityDate() {
        return this.validityDate == null ? this.validityDate = new Date(this.validity) : this.validityDate;
    }

    public boolean isValid() {
        if (this.validity == this.born || this.validity == 0) {
            this.validity = Instant.now().plus(FunnyGuilds.getInstance().getPluginConfiguration().validityStart).toEpochMilli();
            this.markChanged();
        }

        return this.validity >= System.currentTimeMillis();
    }

    public void setValidity(long time) {
        if (time == this.born) {
            this.validity = Instant.now().plus(FunnyGuilds.getInstance().getPluginConfiguration().validityStart).toEpochMilli();
        }
        else {
            this.validity = time;
        }

        this.validityDate = new Date(this.validity);
        this.markChanged();
    }

    public long getProtection() {
        return this.protection;
    }

    public boolean canBeAttacked() {
        return this.getProtection() < System.currentTimeMillis();
    }

    public void setProtection(long protection) {
        this.protection = protection;
        this.markChanged();
    }

    public long getBuild() {
        return this.build;
    }

    public boolean canBuild() {
        if (this.build > System.currentTimeMillis()) {
            return false;
        }

        this.build = 0;
        this.markChanged();
        return true;
    }

    public void setBuild(long time) {
        this.build = time;
        this.markChanged();
    }

    public long getBan() {
        return this.ban;
    }

    public boolean isBanned() {
        return this.ban > System.currentTimeMillis();
    }

    public void setBan(long time) {
        if (time > System.currentTimeMillis()) {
            this.ban = time;
        }
        else {
            this.ban = 0;
        }

        this.markChanged();
    }

    public boolean getPvP() {
        return this.pvp;
    }

    public void setPvP(boolean pvp) {
        this.pvp = pvp;
        this.markChanged();
    }

    public boolean getPvP(Guild alliedGuild) {
        return this.allies.contains(alliedGuild) && this.alliedFFGuilds.contains(alliedGuild.getUUID());
    }

    public void setPvP(Guild alliedGuild, boolean enablePvp) {
        if (enablePvp) {
            this.alliedFFGuilds.add(alliedGuild.getUUID());
        }
        else {
            this.alliedFFGuilds.remove(alliedGuild.getUUID());
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.GUILD;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
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
