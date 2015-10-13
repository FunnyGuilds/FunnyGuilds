package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.basic.util.*;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.core.DataCore;
import org.bukkit.Location;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;

public class Guild implements Basic {

    private UUID uuid;
    private String name;
    private String tag;
    private User owner;
    private User deputy;
    private Rank rank;
    private Region region;
    private Location home;
    private BasicList<User> members;
    private BasicList<Guild> allies;
    private BasicList<Guild> enemies;
    private boolean pvp;
    private long born;
    private long validity;
    private long attacked;
    private long ban;
    private int lives;
    private long build;

    private Guild(UUID uuid) {
        this.uuid = uuid;
        this.born = System.currentTimeMillis();
        this.members = new BasicList<>();
        this.allies = new BasicList<>();
        this.enemies = new BasicList<>();
        GuildUtils.addGuild(this);
    }

    private Guild(String name) {
        this(UUID.randomUUID());
        this.name = name;
    }

    public static Guild get(UUID uuid) {
        for (Guild guild : GuildUtils.getGuilds())
            if (guild.getUUID().equals(uuid))
                return guild;
        return new Guild(uuid);
    }

    public static Guild get(String name) {
        for (Guild guild : GuildUtils.getGuilds())
            if (guild.getName().equalsIgnoreCase(name))
                return guild;
        return new Guild(name);
    }

    public void addLive() {
        this.lives++;
        this.passVariable("lives");
    }

    public void addMember(User user) {
        if (this.members.contains(user))
            return;
        this.members.add(user);
        this.updateRank();
        this.passVariable("members");
    }

    public void addAlly(Guild guild) {
        if (this.allies.contains(guild))
            return;
        this.allies.add(guild);
        this.passVariable("allies");
    }

    public void addEnemy(Guild guild) {
        if (this.enemies.contains(guild))
            return;
        this.enemies.add(guild);
        this.passVariable("enemies");
    }

    public void initUpdate() {
        this.owner.setGuild(this);
        UserUtils.setGuild(this.members.getCollection(), this);
    }

    public void removeLive() {
        this.lives--;
        this.passVariable("lives");
    }

    public void removeMember(User user) {
        this.members.remove(user);
        this.updateRank();
        this.passVariable("members");
    }

    public void removeAlly(Guild guild) {
        this.allies.remove(guild);
        this.passVariable("allies");
    }

    public void removeEnemy(Guild guild) {
        this.enemies.remove(guild);
        this.passVariable("enemies");
    }

    public void delete() {
        GuildUtils.removeGuild(this);
    }

    public boolean isValid() {
        if (this.validity != this.born && this.validity != 0)
            return this.validity > System.currentTimeMillis();
        this.validity = System.currentTimeMillis() + Settings.getInstance().validityStart;
        this.passVariable("validity");
        return true;
    }

    public boolean isBanned() {
        return this.ban > System.currentTimeMillis();
    }

    public boolean canBuild() {
        return this.build > System.currentTimeMillis();
    }

    public void updateRank() {
        this.getRank();
        RankManager.getInstance().update(this);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    @Variable(field = "uuid")
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.passVariable("tag");
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User user) {
        this.owner = user;
        this.addMember(user);
        this.passVariable("owner", "members");
    }

    public User getDeputy() {
        return this.deputy;
    }

    public void setDeputy(User user) {
        this.deputy = user;
        this.passVariable("deputy");
    }

    public Region getRegion() {
        return this.region;
    }

    public void setRegion(Region region) {
        this.region = region;
        if (this.home == null)
            this.home = region.getCenter();
        this.passVariable("region", "home");
    }

    public Location getHome() {
        return this.home;
    }

    public void setHome(Location home) {
        this.home = home;
        this.passVariable("home");
    }

    public Collection<User> getMembers() {
        return this.members.getCollection();
    }

    public void setMembers(Collection<User> members) {
        this.members = new BasicList<>(members);
        this.updateRank();
        this.passVariable("members");
    }

    public Collection<Guild> getAllies() {
        return this.allies.getCollection();
    }

    public void setAllies(Collection<Guild> guilds) {
        this.allies = new BasicList<>(guilds);
        this.passVariable("allies");
    }

    public Collection<Guild> getEnemies() {
        return this.enemies.getCollection();
    }

    public void setEnemies(Collection<Guild> guilds) {
        this.enemies = new BasicList<>(guilds);
        this.passVariable("enemies");
    }

    public boolean getPvP() {
        return this.pvp;
    }

    public void setPvP(boolean b) {
        this.pvp = b;
        this.passVariable("pvp");
    }

    public long getBorn() {
        return this.born;
    }

    public void setBorn(long l) {
        this.born = l;
        this.passVariable("born");
    }

    public long getValidity() {
        return this.validity;
    }

    public void setValidity(long l) {
        if (l == this.born)
            this.validity = System.currentTimeMillis() + Settings.getInstance().validityStart;
        else
            this.validity = l;
        this.passVariable("validity");
    }

    public long getAttacked() {
        return this.attacked;
    }

    public void setAttacked(long l) {
        this.attacked = l;
        this.passVariable("attacked");
    }

    public int getLives() {
        return this.lives;
    }

    public void setLives(int i) {
        this.lives = i;
        this.passVariable("lives");
    }

    public long getBan() {
        return this.ban;
    }

    public void setBan(long l) {
        if (l > System.currentTimeMillis())
            this.ban = l;
        else
            this.ban = 0;
        this.passVariable("ban");
    }

    public long getBuild() {
        return this.build;
    }

    public void setBuild(long time) {
        this.build = time;
    }

    public Rank getRank() {
        if (this.rank != null)
            return this.rank;
        this.rank = new Rank(this);
        RankManager.getInstance().update(this);
        this.passVariable("rank");
        return this.rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
        this.passVariable("rank");
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.passVariable("name");
    }

    @Override
    public BasicType getType() {
        return BasicType.GUILD;
    }

    @Override
    public void passVariable(String... field) {
        DataCore.getInstance().save(this, field);
    }

    @Override
    public Object getVariable(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getVariable(String field, Class<T> clazz) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return (T) f.get(this);
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
        if (o == this)
            return true;
        if (o.getClass() != this.getClass())
            return false;
        Guild guild = (Guild) o;
        if (guild.getName() != null && this.name != null)
            return guild.getName().equalsIgnoreCase(this.name);
        return false;
    }

    @Override
    public String toString() {
        return this.name != null ? this.name : this.uuid.toString();
    }

}
