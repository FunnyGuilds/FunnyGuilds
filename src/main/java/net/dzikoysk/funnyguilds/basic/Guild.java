package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Guild implements Basic {

    private UUID uuid;
    private String name;
    private String tag;
    private User owner;
    private Rank rank;
    private String region;
    private Region regionData;
    private Location home;
    private List<User> members = new ArrayList<>();
    private List<User> deputies = new ArrayList<>();
    private List<String> regions = new ArrayList<>();
    private List<Guild> allies = new ArrayList<>();
    private List<Guild> enemies = new ArrayList<>();
    private Location endercrystal;
    private boolean pvp;
    private long born;
    private long validity;
    private Date validityDate;
    private long attacked;
    private long ban;
    private int lives;
    private long build;
    private boolean changes;

    private Guild(UUID uuid) {
        this.born = System.currentTimeMillis();
        this.uuid = uuid;
        this.changes = true;
        GuildUtils.addGuild(this);
    }

    public Guild(String name) {
        this(UUID.randomUUID());
        this.name = name;
        this.changes = true;
        GuildUtils.addGuild(this);
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

    public void addRegion(String s) {
        if (this.regions.contains(s)) {
            return;
        }
        
        this.regions.add(s);
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
        for (String r : this.regions) {
            Region region = RegionUtils.get(r);
            if (region != null) {
                region.setGuild(this);
            }
        }
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

    @Override
    public boolean changed() {
        boolean c = changes;
        if (c) {
            this.changes = false;
        }
        
        return c;
    }

    @Override
    public void changes() {
        this.changes = true;
    }

    public boolean canBeAttacked() {
        return !(this.getAttacked() != 0 && this.getAttacked() + Settings.getConfig().warWait > System.currentTimeMillis());
    }

    public boolean isSomeoneInRegion() {
        return Settings.getConfig().regionsEnabled && Bukkit.getOnlinePlayers().stream()
                .filter(player -> User.get(player).getGuild() != this)
                .map(player -> RegionUtils.getAt(player.getLocation()))
                .anyMatch(region -> region != null && region.getGuild() == this);
    }

    public boolean isValid() {
        if (this.validity == this.born) {
            this.validity = System.currentTimeMillis() + Settings.getConfig().validityStart;
            this.changes();
        }
        
        if (this.validity == 0) {
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

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
        this.changes();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.changes();
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.changes();
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User user) {
        this.owner = user;
        this.addMember(user);
        this.changes();
    }

    public List<User> getDeputies() {
        return this.deputies;
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

    public void setDeputies(List<User> users) {
        this.deputies = users;
        this.changes();
    }

    public String getRegion() {
        return this.region;
    }

    public Region getRegionData() {
        return this.regionData == null ? this.regionData = RegionUtils.get(this.region) : this.regionData;
    }

    public void setRegion(String s) {
        if (!Settings.getConfig().regionsEnabled) {
            return;
        }
        
        this.region = s;
        if (this.home == null) {
            Region region = Region.get(s);
            this.home = region.getCenter();
        }

        this.regionData = RegionUtils.get(this.region);
        
        this.changes();
    }

    public Location getHome() {
        return this.home;
    }

    public void setHome(Location home) {
        this.home = home;
        this.changes();
    }

    public List<User> getMembers() {
        return this.members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
        this.updateRank();
        this.changes();
    }

    public List<User> getOnlineMembers() {
        return this.members
                .stream()
                .filter(User::isOnline)
                .collect(Collectors.toList());
    }

    public List<String> getRegions() {
        return this.regions;
    }

    public void setRegions(List<String> regions) {
        if (!Settings.getConfig().regionsEnabled) {
            return;
        }
        
        this.regions = regions;
        this.changes();
    }

    public List<Guild> getAllies() {
        return this.allies;
    }

    public void setAllies(List<Guild> guilds) {
        this.allies = guilds;
        this.changes();
    }

    public List<Guild> getEnemies() {
        return this.enemies;
    }

    public void setEnemies(List<Guild> guilds) {
        this.enemies = guilds;
        this.changes();
    }

    public boolean getPvP() {
        return this.pvp;
    }

    public void setPvP(boolean b) {
        this.pvp = b;
        this.changes();
    }

    public long getBorn() {
        return this.born;
    }

    public void setBorn(long l) {
        this.born = l;
        this.changes();
    }

    public long getValidity() {
        return this.validity;
    }

    public Date getValidityDate() {
        return this.validityDate == null ? this.validityDate = new Date(this.validity) : this.validityDate;
    }

    public void setValidity(long l) {
        if (l == this.born) {
            this.validity = System.currentTimeMillis() + Settings.getConfig().validityStart;
        } else {
            this.validity = l;
        }

        this.validityDate = new Date(this.validity);
        this.changes();
    }

    public long getAttacked() {
        return this.attacked;
    }

    public void setAttacked(long l) {
        this.attacked = l;
        this.changes();
    }

    public int getLives() {
        return this.lives;
    }

    public void setLives(int i) {
        this.lives = i;
        this.changes();
    }

    public long getBan() {
        return this.ban;
    }

    public void setBan(long l) {
        if (l > System.currentTimeMillis()) {
            this.ban = l;
        } else {
            this.ban = 0;
        }
        
        this.changes();
    }

    public long getBuild() {
        return this.build;
    }

    public void setBuild(long time) {
        this.build = time;
        this.changes();
    }

    public Rank getRank() {
        if (this.rank != null) {
            return this.rank;
        }
        
        this.rank = new Rank(this);
        RankManager.getInstance().update(this);
        return this.rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
        this.changes();
    }

    public Location getEnderCrystal() {
        return this.endercrystal;
    }

    public void setEnderCrystal(Location loc) {
        this.endercrystal = loc;
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

}
