package net.dzikoysk.funnyguilds.basic;

import com.google.common.base.Charsets;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateUserRequest;
import net.dzikoysk.funnyguilds.element.Dummy;
import net.dzikoysk.funnyguilds.element.IndividualPrefix;
import net.dzikoysk.funnyguilds.element.ScoreboardStack;
import net.dzikoysk.funnyguilds.util.commons.StringUtils;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class User implements Basic {

    private static final Set<UUID> ONLINE_USERS_CACHE = new HashSet<>();

    private final UUID uuid;
    private String name;
    private Guild guild;
    private Rank rank;
    private Scoreboard scoreboard;
    private IndividualPrefix prefix;
    private Dummy dummy;
    private long ban;
    private String reason;
    private User lastAttacker;
    private User lastVictim;
    private long lastAttackerTime;
    private long lastVictimTime;
    private BukkitTask teleportation;
    private long notification;
    private boolean enter;
    private boolean bypass;
    private boolean changes;
    private boolean spy;
    
    private final Map<User, Double> damage = new HashMap<>();

    private User(UUID uuid) {
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
        this.changes = true;
        this.updateCache();
    }

    private User(String name) {
        this.name = name;
        this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.name).getBytes(Charsets.UTF_8));
        this.changes = true;
    }

    private User(Player player) {
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.changes = true;
        this.updateCache();
    }

    public static User get(UUID uuid) {
        User u = UserUtils.get(uuid);
        return u != null ? u : new User(uuid);
    }

    public static User get(Player player) {
        User u = UserUtils.get(player.getUniqueId());
        return u != null ? u : new User(player);
    }

    public static User get(OfflinePlayer offline) {
        User u = UserUtils.get(offline.getName());
        return u != null ? u : new User(offline.getName());
    }

    public static User get(String name) {
        User u = UserUtils.get(name);
        return u != null ? u : new User(name);
    }

    public void removeGuild() {
        this.guild = null;
        this.changes();

        // IndependentThread.action(ActionType.RANK_UPDATE_USER, this);
        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new RankUpdateUserRequest(this));
    }

    public boolean hasGuild() {
        return this.guild != null;
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

    private void updateCache() {
        UserUtils.addUser(this);
    }

    public boolean isOwner() {
        if (!hasGuild()) {
            return false;
        }
        
        return this.guild.getOwner().equals(this);
    }

    public boolean isDeputy() {
        if (!hasGuild()) {
            return false;
        }
        
        return this.guild.getDeputies().contains(this);
    }

    public boolean isOnline() {
        if (this.name == null) {
            return false;
        }
        
        if (!ONLINE_USERS_CACHE.contains(this.uuid)) {
            final Player player = Bukkit.getPlayer(this.uuid);
            if (player != null) {
                ONLINE_USERS_CACHE.add(this.uuid);
                return true;
            }
            
            return false;
        }
        
        return true;
    }

    public boolean isBanned() {
        return this.ban != 0;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
        this.changes();
    }

    public void setName(String name) {
        this.name = name;
        this.changes();
        this.updateCache();
    }

    public synchronized Scoreboard getScoreboard() {
        if (this.scoreboard == null) {
            this.scoreboard = ScoreboardStack.pull();
        }
        
        return this.scoreboard;
    }

    public synchronized void setScoreboard(Scoreboard sb) {
        this.scoreboard = sb;
    }

    public IndividualPrefix getIndividualPrefix() {
        if (this.prefix == null) {
            new IndividualPrefix(this);
        }
        
        return this.prefix;
    }

    public void setIndividualPrefix(IndividualPrefix prefix) {
        this.prefix = prefix;
    }

    public Dummy getDummy() {
        if (this.dummy == null) {
            this.dummy = new Dummy(this);
        }
        
        return this.dummy;
    }

    public void setDummy(Dummy dummy) {
        this.dummy = dummy;
    }

    public Rank getRank() {
        if (this.rank != null) {
            return this.rank;
        }
        
        this.rank = new Rank(this);
        RankManager.getInstance().update(this);
        this.changes();
        
        return this.rank;
    }

    public void setRank(Rank r) {
        this.rank = r;
        this.changes();
    }

    public long getBan() {
        return this.ban;
    }

    public void setBan(long l) {
        this.ban = l;
        this.changes();
    }

    public String getReason() {
        if (this.reason != null) {
            return StringUtils.colored(this.reason);
        }
        
        return "";
    }

    public void setReason(String s) {
        this.reason = s;
        this.changes();
    }

    public long getNotificationTime() {
        return this.notification;
    }

    public void setNotificationTime(long time) {
        this.notification = time;
    }

    public boolean getEnter() {
        return this.enter;
    }

    public void setEnter(boolean b) {
        this.enter = b;
    }

    public User getLastVictim() {
        return this.lastVictim;
    }

    public void setLastVictim(User user) {
        this.lastVictim = user;
        this.lastVictimTime = System.currentTimeMillis();
    }

    public User getLastAttacker() {
        return this.lastAttacker;
    }

    public void setLastAttacker(User user) {
        this.lastAttacker = user;
        this.lastAttackerTime = System.currentTimeMillis();
    }

    public long getLastVictimTime() {
        return this.lastVictimTime;
    }

    public long getLastAttackerTime() {
        return this.lastAttackerTime;
    }
    
    public Map<User, Double> getDamage() {
        return new HashMap<>(this.damage);
    }
    
    public Double getTotalDamage() {
        double dmg = 0.0D;
        for (double d : this.damage.values()) {
            dmg += d;
        }
        
        return dmg;
    }
    
    public void addDamage(User user, double damage) {
        Double currentDamage = this.damage.get(user);
        this.damage.put(user, (currentDamage == null ? 0.0D : currentDamage) + damage);
    }
    
    public double killedBy(User user) {
        if (user == null) {
            return 0.0D;
        }
        Double dmg = this.damage.remove(user);
        return dmg == null ? 0.0D : dmg;
    }
    
    public boolean isAssisted() {
        return !this.damage.isEmpty();
    }
    
    public void clearDamage() {
        this.damage.clear();
    }

    public BukkitTask getTeleportation() {
        return this.teleportation;
    }

    public void setTeleportation(BukkitTask task) {
        this.teleportation = task;
    }

    public Player getPlayer() {
        if (!isOnline()) {
            return null;
        }
        
        return Bukkit.getPlayer(this.uuid);
    }

    public boolean getBypass() {
        return this.bypass;
    }

    public void setBypass(boolean b) {
        this.bypass = b;
    }

    public int getPing() {
        int ping = 0;
        Player p = getPlayer();
        
        if (p == null) {
            return ping;
        }
        
        try {
            Class<?> craftPlayer = Reflections.getCraftBukkitClass("entity.CraftPlayer");
            Object cp = craftPlayer.cast(p);
            Object handle = craftPlayer.getMethod("getHandle").invoke(cp);
            ping = (int) handle.getClass().getField("ping").get(handle);
        } catch (Exception e) {
            if (FunnyLogger.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        
        return ping;
    }

    public boolean isSpy() {
        return this.spy;
    }

    public void setSpy(boolean spy) {
        this.spy = spy;
    }

    @Override
    public BasicType getType() {
        return BasicType.USER;
    }

    public void removeFromCache() {
        ONLINE_USERS_CACHE.remove(this.uuid);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (uuid == null ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (o.getClass() != this.getClass()) {
            return false;
        }
        
        User u = (User) o;
        if (!u.getUUID().equals(this.uuid)) {
            return false;
        }
        
        return u.getName().equals(this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }
	
}
