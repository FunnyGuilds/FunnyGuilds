package net.dzikoysk.funnyguilds.basic.guild;

import net.dzikoysk.funnyguilds.basic.AbstractBasic;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.rank.Rank;
import net.dzikoysk.funnyguilds.basic.rank.RankManager;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Guild extends AbstractBasic {

    private UUID uuid;
    private String name;
    private String tag;
    private User owner;
    private Rank rank;
    private Region region;
    private Location home;
    private Set<User> members = new HashSet<>();
    private Set<User> deputies = new HashSet<>();
    private Set<Guild> allies = new HashSet<>();
    private Set<Guild> enemies = new HashSet<>();
    private Location enderCrystal;
    private boolean pvp;
    private long born;
    private long validity;
    private Date validityDate;
    private long attacked;
    private long ban;
    private int lives;
    private long build;
    private long additionalProtection;

    private Guild(UUID uuid) {
        this.born = System.currentTimeMillis();
        this.uuid = uuid;
        GuildUtils.addGuild(this);
    }

    public Guild(String name) {
        this(UUID.randomUUID());
        this.name = name;
        GuildUtils.addGuild(this);
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
        this.changes();
    }

    public void addMember(User user) {
        if (this.members.contains(user)) {
            return;
        }

        this.members.add(user);
        this.updateRank();
        this.changes();
    }

    public void addAlly(Guild guild) {
        this.changes();
        if (this.allies.contains(guild)) {
            return;
        }

        this.allies.add(guild);
    }

    public void addEnemy(Guild guild) {
        this.changes();
        if (this.enemies.contains(guild)) {
            return;
        }

        this.enemies.add(guild);
    }

    public void deserializationUpdate() {
        this.owner.setGuild(this);
        UserUtils.setGuild(this.members, this);
    }

    public void removeLive() {
        this.lives--;
        this.changes();
    }

    public void removeMember(User user) {
        this.deputies.remove(user);
        this.members.remove(user);
        this.updateRank();
        this.changes();
    }

    public void removeAlly(Guild guild) {
        this.allies.remove(guild);
        this.changes();
    }

    public void removeEnemy(Guild guild) {
        this.enemies.remove(guild);
        this.changes();
    }

    public void delete() {
        GuildUtils.removeGuild(this);
    }

    public boolean canBuild() {
        if (this.build > System.currentTimeMillis()) {
            return false;
        }

        this.build = 0;
        this.changes();
        return true;
    }

    public void updateRank() {
        this.getRank();
        RankManager.getInstance().update(this);
    }

    public boolean canBeAttacked() {
        return this.getProtectionEndTime() < System.currentTimeMillis() && this.additionalProtection < System.currentTimeMillis();
    }

    public void addDeputy(User user) {
        if (this.deputies.contains(user)) {
            return;
        }

        this.deputies.add(user);
        this.changes();
    }

    public void removeDeputy(User user) {
        this.deputies.remove(user);
        this.changes();
    }

    public void setAdditionalProtection(long timestamp) {
        this.additionalProtection = timestamp;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
        this.changes();
    }

    public void setName(String name) {
        this.name = name;
        this.changes();
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.changes();
    }

    public void setOwner(User user) {
        this.owner = user;
        this.addMember(user);
        this.changes();
    }

    public void setDeputies(Set<User> users) {
        this.deputies = users;
        this.changes();
    }

    public void setRegion(Region region) {
        if (!Settings.getConfig().regionsEnabled) {
            return;
        }

        this.region = region;
        this.region.setGuild(this);

        if (this.home == null) {
            this.home = region.getCenter();
        }

        this.changes();
    }

    public void setHome(Location home) {
        this.home = home;
        this.changes();
    }

    public void setMembers(Set<User> members) {
        this.members = members;
        this.updateRank();
        this.changes();
    }

    public void setAllies(Set<Guild> guilds) {
        this.allies = guilds;
        this.changes();
    }

    public void setEnemies(Set<Guild> guilds) {
        this.enemies = guilds;
        this.changes();
    }

    public void setPvP(boolean b) {
        this.pvp = b;
        this.changes();
    }

    public void setBorn(long l) {
        this.born = l;
        this.changes();
    }

    public void setValidity(long l) {
        if (l == this.born) {
            this.validity = System.currentTimeMillis() + Settings.getConfig().validityStart;
        }
        else {
            this.validity = l;
        }

        this.validityDate = new Date(this.validity);
        this.changes();
    }

    public void setAttacked(long l) {
        this.attacked = l;
        this.changes();
    }

    public void setLives(int i) {
        this.lives = i;
        this.changes();
    }

    public void setBan(long l) {
        if (l > System.currentTimeMillis()) {
            this.ban = l;
        }
        else {
            this.ban = 0;
        }

        this.changes();
    }

    public void setBuild(long time) {
        this.build = time;
        this.changes();
    }

    public void setRank(Rank rank) {
        this.rank = rank;
        this.changes();
    }

    public void setEnderCrystal(Location loc) {
        this.enderCrystal = loc;
    }

    public long getProtectionEndTime() {
        return this.attacked == this.born ? this.attacked + Settings.getConfig().warProtection : this.attacked + Settings.getConfig().warWait;
    }

    public long getAdditionalProtectionEndTime() {
        return this.additionalProtection;
    }

    public boolean isSomeoneInRegion() {
        return Settings.getConfig().regionsEnabled && Bukkit.getOnlinePlayers().stream()
                .filter(player -> User.get(player).getGuild() != this)
                .map(player -> RegionUtils.getAt(player.getLocation()))
                .anyMatch(region -> region != null && region.getGuild() == this);
    }

    public boolean isValid() {
        if (this.validity == this.born || this.validity == 0) {
            this.validity = System.currentTimeMillis() + Settings.getConfig().validityStart;
            this.changes();
        }

        return this.validity >= System.currentTimeMillis();
    }

    public boolean isBanned() {
        if (this.ban > System.currentTimeMillis()) {
            return true;
        }

        this.ban = 0;
        this.changes();
        return false;
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
        return this.enemies;
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

    public long getAttacked() {
        return this.attacked;
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
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (o.getClass() != this.getClass()) {
            return false;
        }

        Guild guild = (Guild) o;
        if (guild.getName() != null && this.name != null) {
            return guild.getName().equalsIgnoreCase(this.name);
        }

        return false;
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

        return new Guild(uuid);
    }

    public static Guild getOrCreate(String name) {
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getName().equalsIgnoreCase(name)) {
                return guild;
            }
        }

        return new Guild(name);
    }

}
