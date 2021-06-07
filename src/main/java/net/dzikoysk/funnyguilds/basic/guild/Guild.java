package net.dzikoysk.funnyguilds.basic.guild;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.AbstractBasic;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.rank.Rank;
import net.dzikoysk.funnyguilds.basic.rank.RankManager;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserManager;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Guild extends AbstractBasic {

    private final UUID uuid;

    private String     name;
    private String     tag;
    private User       owner;
    private Rank       rank;
    private Region     region;
    private Location   home;
    private Set<User>  members;
    private Set<User>  deputies;
    private Set<Guild> allies;
    private Set<Guild> enemies;
    private Location   enderCrystal;
    private boolean    pvp;
    private long       born;
    private long       validity;
    private Date       validityDate;
    private long       protection;
    private long       ban;
    private int        lives;
    private long       build;
    private Set<UUID>  alliedFFGuilds;

    private Guild(UUID uuid) {
        this.uuid = uuid;

        this.born = System.currentTimeMillis();
        this.members = ConcurrentHashMap.newKeySet();
        this.deputies = ConcurrentHashMap.newKeySet();
        this.allies = ConcurrentHashMap.newKeySet();
        this.enemies = ConcurrentHashMap.newKeySet();
        this.alliedFFGuilds = ConcurrentHashMap.newKeySet();
    }

    public Guild(String name) {
        this(UUID.randomUUID());
        this.name = name;
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
        this.updateRank();
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
        UserUtils.setGuild(this.members, this);
    }

    public void removeLive() {
        this.lives--;
        this.markChanged();
    }

    public void removeMember(User user) {
        this.deputies.remove(user);
        this.members.remove(user);
        this.updateRank();
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

    public void delete() {
        GuildUtils.removeGuild(this);
    }

    public boolean canBuild() {
        if (this.build > System.currentTimeMillis()) {
            return false;
        }

        this.build = 0;
        this.markChanged();
        return true;
    }

    public void updateRank() {
        this.getRank();
        RankManager.getInstance().update(this);
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
        if (! FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            return;
        }

        this.region = region;
        this.region.setGuild(this);

        if (this.home == null) {
            this.home = region.getCenter();
        }

        this.markChanged();
    }

    public void setHome(Location home) {
        this.home = home;
        this.markChanged();
    }

    public void setMembers(Set<User> members) {
        this.members = Collections.synchronizedSet(members);
        this.updateRank();
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
            this.validity = System.currentTimeMillis() + FunnyGuilds.getInstance().getPluginConfiguration().validityStart;
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

    public void setRank(Rank rank) {
        this.rank = rank;
        this.markChanged();
    }

    public void setEnderCrystal(Location loc) {
        this.enderCrystal = loc;
    }

    public boolean isSomeoneInRegion() {
        return FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled && Bukkit.getOnlinePlayers().stream()
                .filter(player -> UserManager.getInstance().getUser(player).getGuild() != this)
                .map(player -> RegionUtils.getAt(player.getLocation()))
                .anyMatch(region -> region != null && region.getGuild() == this);
    }

    public boolean isValid() {
        if (this.validity == this.born || this.validity == 0) {
            this.validity = System.currentTimeMillis() + FunnyGuilds.getInstance().getPluginConfiguration().validityStart;
            this.markChanged();
        }

        return this.validity >= System.currentTimeMillis();
    }

    public boolean isBanned() {
        return this.ban > System.currentTimeMillis();
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

    public Rank getRank() {
        if (this.rank != null) {
            return this.rank;
        }

        this.rank = new Rank(this);
        RankManager.getInstance().update(this);
        return this.rank;
    }

    public Location getEnderCrystal() {
        return this.enderCrystal;
    }

    @Override
    public BasicType getType() {
        return BasicType.GUILD;
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

    public static Guild getOrCreate(UUID uuid) {
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getUUID().equals(uuid)) {
                return guild;
            }
        }

        final Guild newGuild = new Guild(uuid);
        GuildUtils.addGuild(newGuild);

        return newGuild;
    }

    public static Guild getOrCreate(String name) {
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getName().equalsIgnoreCase(name)) {
                return guild;
            }
        }

        final Guild newGuild = new Guild(name);
        GuildUtils.addGuild(newGuild);

        return newGuild;
    }

}
