package net.dzikoysk.funnyguilds.guild;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import panda.std.stream.PandaStream;

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
    private Set<UUID> alliedPvPGuilds = ConcurrentHashMap.newKeySet();

    private Instant born;
    private Instant validity;
    private Instant protection;
    private Option<Instant> build = Option.none();
    private Option<Instant> ban = Option.none();

    private boolean pvp;

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

    /**
     * @return region of the guild.
     */
    public Option<Region> getRegion() {
        return this.region;
    }

    public boolean hasRegion() {
        return this.region.isPresent();
    }

    public void setRegion(@Nullable Region region) {
        this.region = Option.of(region);
        this.region.peek(peekRegion -> peekRegion.setGuild(this));
        this.markChanged();
    }

    public Option<Location> getCenter() {
        return this.region
                .map(Region::getCenter)
                .map(Location::clone);
    }

    public Option<Location> getEnderCrystal() {
        return this.getCenter().map(location -> location.add(0.5D, -1.0D, 0.5D));
    }

    /**
     * @return home location of the guild
     */
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

    public void teleportHome(Player player) {
        this.home.peek(player::teleport);
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

    public Set<User> getOnlineMembers() {
        return PandaStream.of(this.members)
                .filter(User::isOnline)
                .toSet();
    }

    public boolean isMember(User user) {
        return this.members.contains(user);
    }

    public void setMembers(Set<User> members) {
        this.members = Collections.synchronizedSet(members);
        this.markChanged();
    }

    public void addMember(User user) {
        this.members.add(user);
        this.markChanged();
    }

    public void removeMember(User user) {
        this.members.remove(user);
        this.deputies.remove(user);
        this.markChanged();
    }

    public Set<User> getDeputies() {
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

    public Set<Guild> getAllies() {
        return this.allies;
    }

    public boolean hasAllies() {
        return !this.allies.isEmpty();
    }

    public boolean isAlly(@Nullable Guild guild) {
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

    public Set<Guild> getEnemies() {
        return this.enemies;
    }

    public boolean hasEnemies() {
        return !this.enemies.isEmpty();
    }

    public boolean isEnemy(@Nullable Guild guild) {
        return this.enemies.contains(guild);
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

    public Option<Instant> getBuild() {
        return this.build;
    }

    public boolean canBuild() {
        if (this.build.is(build -> build.isAfter(Instant.now()))) {
            return false;
        }

        this.build = Option.none();
        this.markChanged();
        return true;
    }

    public void setBuild(@Nullable Instant time) {
        if (time != null && time.isBefore(Instant.now())) {
            time = null;
        }

        this.build = Option.of(time);
        this.markChanged();
    }

    public Option<Instant> getBan() {
        return this.ban;
    }

    public boolean isBanned() {
        if (this.ban.is(ban -> ban.isAfter(Instant.now()))) {
            return true;
        }

        this.ban = Option.none();
        this.markChanged();
        return false;
    }

    public void setBan(@Nullable Instant time) {
        if (time != null && time.isBefore(Instant.now())) {
            time = null;
        }

        this.ban = Option.of(time);
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
