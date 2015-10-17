package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.data.core.DataCore;
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

import java.lang.reflect.Field;
import java.util.UUID;

public class User implements Basic {

    private final UUID uuid;
    private String name;
    private Guild guild;
    private Rank rank;
    private long ban;
    private String reason;
    private Scoreboard scoreboard;
    private PlayerList list;
    private IndividualPrefix prefix;
    private Dummy dummy;
    private User lastAttacker;
    private User lastVictim;
    private long lastAttackerTime;
    private long lastVictimTime;
    private BukkitTask teleportation;
    private long notification;
    private boolean enter;

    private User(UUID uuid) {
        this.uuid = uuid;
        this.passVariable("uuid");
        UserUtils.addUser(this);
    }

    private User(OfflineUser offline) {
        this(offline.getUniqueId());
        this.name = offline.getName();
        this.passVariable("name");
    }

    private User(String name) {
        this(new OfflineUser(name));
    }

    public static User get(UUID uuid) {
        for (User u : UserUtils.getUsers())
            if (uuid.equals(u.getUUID()))
                return u;
        return new User(uuid);
    }

    public static User get(String name) {
        for (User u : UserUtils.getUsers())
            if (u.getName().equalsIgnoreCase(name))
                return u;
        return new User(name);
    }

    public static User get(OfflinePlayer player) {
        for (User u : UserUtils.getUsers())
            if (u.getName().equalsIgnoreCase(player.getName()))
                return u;
        return new User(player.getName());
    }

    public void removeGuild() {
        this.guild = null;
        IndependentThread.action(ActionType.RANK_UPDATE_USER, this);
        this.passVariable("guild");
    }

    public boolean hasGuild() {
        return this.guild != null;
    }

    public boolean isOwner() {
        return hasGuild() && this.guild.getOwner().equals(this);
    }

    public boolean isDeputy() {
        return hasGuild() && guild.getDeputy() != null && this.guild.getDeputy().equals(this);
    }

    public boolean isOnline() {
        if (this.name == null)
            return false;
        Player player = Bukkit.getPlayer(this.name);
        return player != null && player.isOnline();
    }

    public boolean isBanned() {
        return this.ban != 0;
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
        this.passVariable("name");
    }

    public Guild getGuild() {
        return this.guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
        this.passVariable("guild");
    }

    public synchronized Scoreboard getScoreboard() {
        if (this.scoreboard == null)
            this.scoreboard = ScoreboardStack.pull();
        return this.scoreboard;
    }

    public void setScoreboard(Scoreboard sb) {
        this.scoreboard = sb;
    }

    public IndividualPrefix getIndividualPrefix() {
        if (this.prefix == null)
            new IndividualPrefix(this);
        return this.prefix;
    }

    public void setIndividualPrefix(IndividualPrefix prefix) {
        this.prefix = prefix;
    }

    public PlayerList getPlayerList() {
        if (this.list == null)
            new PlayerList(this);
        return this.list;
    }

    public void setPlayerList(PlayerList pl) {
        this.list = pl;
    }

    public Dummy getDummy() {
        if (this.dummy == null)
            this.dummy = new Dummy(this);
        return this.dummy;
    }

    public void setDummy(Dummy dummy) {
        this.dummy = dummy;
    }

    public Rank getRank() {
        if (this.rank != null)
            return this.rank;
        this.rank = new Rank(this);
        RankManager.getInstance().update(this);
        this.passVariable("rank");
        return this.rank;
    }

    public void setRank(Rank r) {
        this.rank = r;
        this.passVariable("rank");
    }

    public long getBan() {
        return this.ban;
    }

    public void setBan(long l) {
        this.ban = l;
        this.passVariable("ban");
    }

    public String getReason() {
        return this.reason != null ? ChatColor.translateAlternateColorCodes('&', this.reason) : "";
    }

    public void setReason(String s) {
        this.reason = s;
        this.passVariable("reason");
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

    public BukkitTask getTeleportation() {
        return this.teleportation;
    }

    public void setTeleportation(BukkitTask task) {
        this.teleportation = task;
    }

    public Player getPlayer() {
        return this.name != null ? Bukkit.getPlayer(this.name) : null;
    }

    public OfflineUser getOfflineUser() {
        return this.name != null ? new OfflineUser(this.name) : null;
    }

    public int getPing() {
        int ping = 0;
        Player p = getPlayer();
        if (p == null)
            return ping;
        try {
            Class<?> craftPlayer = Reflections.getBukkitClass("entity.CraftPlayer");
            Object cp = craftPlayer.cast(p);
            Object handle = craftPlayer.getMethod("getHandle").invoke(cp);
            ping = (int) handle.getClass().getField("ping").get(handle);
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause()))
                e.printStackTrace();
        }
        return ping;
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
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;
        User u = (User) o;
        if (u.getUUID() != this.uuid)
            return false;
        if (u.getName() != this.name)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return this.name != null ? this.name : this.uuid.toString();
    }

}
