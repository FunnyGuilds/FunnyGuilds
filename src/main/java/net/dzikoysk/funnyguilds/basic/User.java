package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.util.element.Dummy;
import net.dzikoysk.funnyguilds.util.element.IndividualPrefix;
import net.dzikoysk.funnyguilds.util.element.PlayerList;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import net.dzikoysk.funnyguilds.util.runnable.ScoreboardStack;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.util.UUID;

public class User implements Basic {

    private UUID uuid;
    private String name;
    private Guild guild;
    private Rank rank;
    private Scoreboard scoreboard;
    private PlayerList list;
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

    private User(UUID uuid) {
        this.uuid = uuid;
        this.changes = true;
        this.updateCache();
    }

    private User(String name) {
        this(new OfflineUser(name));
        this.changes = true;
    }

    private User(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.changes = true;
        this.updateCache();
    }

    private User(OfflineUser offline) {
        this.uuid = offline.getUniqueId();
        this.name = offline.getName();
        this.changes = true;
        this.updateCache();
    }

    public void removeGuild() {
        this.guild = null;
        IndependentThread.action(ActionType.RANK_UPDATE_USER, this);
        this.changes();
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

    private void updateCache()
    {
        UserUtils.addUser(this);
    }

    public void setName(String name) {
        this.name = name;
        this.changes();
        this.updateCache();
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
        this.changes();
    }

    public void setScoreboard(Scoreboard sb) {
        this.scoreboard = sb;
    }

    public void setIndividualPrefix(IndividualPrefix prefix) {
        this.prefix = prefix;
    }

    public void setPlayerList(PlayerList pl) {
        this.list = pl;
    }

    public void setDummy(Dummy dummy) {
        this.dummy = dummy;
    }

    public void setRank(Rank r) {
        this.rank = r;
        this.changes();
    }

    public void setBan(long l) {
        this.ban = l;
        this.changes();
    }

    public void setReason(String s) {
        this.reason = s;
        this.changes();
    }

    public void setEnter(boolean b) {
        this.enter = b;
    }

    public void setLastVictim(User user) {
        this.lastVictim = user;
        this.lastVictimTime = System.currentTimeMillis();
    }

    public void setLastAttacker(User user) {
        this.lastAttacker = user;
        this.lastAttackerTime = System.currentTimeMillis();
    }

    public void setNotificationTime(long time) {
        this.notification = time;
    }

    public void setTeleportation(BukkitTask task) {
        this.teleportation = task;
    }

    public void setBypass(boolean b) {
        this.bypass = b;
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
        if (this.guild.getDeputy() == null) {
            return false;
        }
        return this.guild.getDeputy().equals(this);
    }

    public boolean isOnline() {
        if (this.name == null) {
            return false;
        }
        Player player = Bukkit.getPlayer(this.name);
        return player != null && player.getName().equals(this.name) && player.isOnline();
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

    public synchronized Scoreboard getScoreboard() {
        if (this.scoreboard == null) {
            this.scoreboard = ScoreboardStack.pull();
        }
        return this.scoreboard;
    }

    public IndividualPrefix getIndividualPrefix() {
        if (this.prefix == null) {
            new IndividualPrefix(this);
        }
        return this.prefix;
    }

    public PlayerList getPlayerList() {
        if (this.list == null) {
            new PlayerList(this);
        }
        return this.list;
    }

    public Dummy getDummy() {
        if (this.dummy == null) {
            this.dummy = new Dummy(this);
        }
        return this.dummy;
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

    public long getBan() {
        return this.ban;
    }

    public String getReason() {
        if (this.reason != null) {
            return ChatColor.translateAlternateColorCodes('&', this.reason);
        }
        return "";
    }

    public long getNotificationTime() {
        return this.notification;
    }

    public boolean getEnter() {
        return this.enter;
    }

    public User getLastVictim() {
        return this.lastVictim;
    }

    public User getLastAttacker() {
        return this.lastAttacker;
    }

    public long getLastVictimTime() {
        return this.lastVictimTime;
    }

    public long getLastAttackerTime() {
        return this.lastAttackerTime;
    }

    public BukkitTask getTeleportation() {
        return this.teleportation;
    }

    public Player getPlayer() {
        if (!isOnline()) {
            return null;
        }
        return Bukkit.getPlayer(this.name);
    }

    public OfflineUser getOfflineUser() {
        return new OfflineUser(this.name);
    }

    public boolean getBypass() {
        return this.bypass;
    }

    public int getPing() {
        int ping = 0;
        Player p = getPlayer();
        if (p == null) {
            return ping;
        }
        try {
            Class<?> craftPlayer = Reflections.getBukkitClass("entity.CraftPlayer");
            Object cp = craftPlayer.cast(p);
            Object handle = craftPlayer.getMethod("getHandle").invoke(cp);
            ping = (int) handle.getClass().getField("ping").get(handle);
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        return ping;
    }

    @Override
    public BasicType getType() {
        return BasicType.USER;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
        if (u.getUUID() != this.uuid) {
            return false;
        }
        return u.getName() == this.name;
    }

    @Override
    public String toString() {
        return this.name;
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
}
