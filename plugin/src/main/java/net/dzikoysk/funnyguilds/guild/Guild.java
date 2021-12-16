package net.dzikoysk.funnyguilds.guild;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class Guild extends AbstractMutableEntity {

    private final UUID uuid;

    private String name;
    private String tag;

    private GuildRank rank;
    private int lives;

    private Region region;
    private Location home;

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

    public Guild(String name) {
        this(UUID.randomUUID());
        this.name = name;
    }

    public Guild(String name, String tag) {
        this(UUID.randomUUID());
        this.name = name;
        this.tag = tag;
    }

    public Guild(UUID uuid, String name, String tag) {
        this(uuid);
        this.name = name;
        this.tag = tag;
    }

    public void broadcast(String message) {
        for (User user : this.getOnlineMembers()) {
            if (user.getPlayer() == null) {
                continue;
            }

            user.getPlayer().sendMessage(message);
        }
    }

    public void addLive() {
        this.lives++;
        this.markChanged();
    }

    public void addMember(User user) {
        if (this.members.contains(user)) {
            return;
        }

        this.members.add(user);
        this.markChanged();
    }

    public void addAlly(Guild guild) {
        this.markChanged();
        if (this.allies.contains(guild)) {
            return;
        }

        this.allies.add(guild);
    }

    public void addEnemy(Guild guild) {
        if (this.enemies.contains(guild)) {
            return;
        }

        this.enemies.add(guild);
        this.markChanged();
    }

    public void deserializationUpdate() {
        this.owner.setGuild(this);
        this.members.forEach(user -> user.setGuild(this));
    }

    public void removeLive() {
        this.lives--;
        this.markChanged();
    }

    public void removeMember(User user) {
        this.deputies.remove(user);
        this.members.remove(user);
        this.markChanged();
    }

    public void removeAlly(Guild guild) {
        this.allies.remove(guild);
        this.markChanged();
    }

    public void removeEnemy(Guild guild) {
        this.enemies.remove(guild);
        this.markChanged();
    }

    public boolean canBuild() {
        if (this.build > System.currentTimeMillis()) {
            return false;
        }

        this.build = 0;
        this.markChanged();
        return true;
    }

    public boolean canBeAttacked() {
        return this.getProtection() < System.currentTimeMillis();
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

    public void setName(String name) {
        this.name = name;
        this.markChanged();
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.markChanged();
    }

    public void setOwner(User user) {
        this.owner = user;
        this.addMember(user);
        this.markChanged();
    }

    public void setDeputies(Set<User> users) {
        this.deputies = users;
        this.markChanged();
    }

    public void setRegion(Region region) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (!config.regionsEnabled) {
            return;
        }

        this.region = region;
        this.region.setGuild(this);

        Location center = region.getCenter();

        if (this.home == null) {
            this.home = center.clone();
        }

        this.markChanged();
    }

    public void setHome(Location home) {
        this.home = home;
        this.markChanged();
    }

    public void setMembers(Set<User> members) {
        this.members = Collections.synchronizedSet(members);
        this.markChanged();
    }

    public void setAllies(Set<Guild> guilds) {
        this.allies = guilds;
        this.markChanged();
    }

    public void setEnemies(Set<Guild> guilds) {
        this.enemies = guilds;
        this.markChanged();
    }

    public void setPvP(boolean b) {
        this.pvp = b;
        this.markChanged();
    }

    public void setPvP(Guild alliedGuild, boolean enablePvp) {
        if (enablePvp) {
            this.alliedFFGuilds.add(alliedGuild.getUUID());
        }
        else {
            this.alliedFFGuilds.remove(alliedGuild.getUUID());
        }
    }

    public boolean getPvP(Guild alliedGuild) {
        return this.allies.contains(alliedGuild) && this.alliedFFGuilds.contains(alliedGuild.getUUID());
    }

    public void setBorn(long l) {
        this.born = l;
        this.markChanged();
    }

    public void setValidity(long l) {
        if (l == this.born) {
            this.validity = Instant.now().plus(FunnyGuilds.getInstance().getPluginConfiguration().validityStart).toEpochMilli();
        }
        else {
            this.validity = l;
        }

        this.validityDate = new Date(this.validity);
        this.markChanged();
    }

    public void setProtection(long protection) {
        this.protection = protection;
        this.markChanged();
    }

    public void setLives(int i) {
        this.lives = i;
        this.markChanged();
    }

    public void setBan(long l) {
        if (l > System.currentTimeMillis()) {
            this.ban = l;
        }
        else {
            this.ban = 0;
        }

        this.markChanged();
    }

    public void setBuild(long time) {
        this.build = time;
        this.markChanged();
    }

    public boolean isSomeoneInRegion() {
        FunnyGuilds plugin = FunnyGuilds.getInstance();

        return plugin.getPluginConfiguration().regionsEnabled && PandaStream.of(Bukkit.getOnlinePlayers())
                .filter(player -> {
                    Option<User> user = plugin.getUserManager().findByPlayer(player);
                    return user.isDefined() && user.get().getGuild() != this;
                })
                .flatMap(player -> plugin.getRegionManager().findRegionAtLocation(player.getLocation()))
                .find(region -> region != null && region.getGuild() == this)
                .isPresent();
    }

    public boolean isValid() {
        if (this.validity == this.born || this.validity == 0) {
            this.validity = Instant.now().plus(FunnyGuilds.getInstance().getPluginConfiguration().validityStart).toEpochMilli();
            this.markChanged();
        }

        return this.validity >= System.currentTimeMillis();
    }

    public boolean isBanned() {
        return this.ban > System.currentTimeMillis();
    }

    public boolean isRanked() {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        return members.size() >= config.minMembersToInclude;
    }

    public boolean hasDeputies() {
        return !this.deputies.isEmpty();
    }

    public boolean hasAllies() {
        return !this.allies.isEmpty();
    }

    public boolean hasEnemies() {
        return !this.enemies.isEmpty();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getTag() {
        return this.tag;
    }

    public User getOwner() {
        return this.owner;
    }

    public Set<User> getDeputies() {
        return this.deputies;
    }

    public Region getRegion() {
        return region;
    }

    public Location getHome() {
        return this.home;
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

    public Set<Guild> getAllies() {
        return this.allies;
    }

    public Set<Guild> getEnemies() {
        return enemies;
    }

    public boolean getPvP() {
        return this.pvp;
    }

    public long getBorn() {
        return this.born;
    }

    public long getValidity() {
        return this.validity;
    }

    public Date getValidityDate() {
        return this.validityDate == null ? this.validityDate = new Date(this.validity) : this.validityDate;
    }

    public long getProtection() {
        return this.protection;
    }

    public int getLives() {
        return this.lives;
    }

    public long getBan() {
        return this.ban;
    }

    public long getBuild() {
        return this.build;
    }

    public GuildRank getRank() {
        return this.rank;
    }

    public Option<Location> getCenter() {
        return Option.of(this.region)
                .map(Region::getCenter)
                .map(Location::clone);
    }

    public Option<Location> getEnderCrystal() {
        return this.getCenter().map(location -> location.add(0.5D, -1.0D, 0.5D));
    }

    @Override
    public EntityType getType() {
        return EntityType.GUILD;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + (uuid == null ? 0 : uuid.hashCode());

        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        return ((Guild) obj).getUUID().equals(this.uuid);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
