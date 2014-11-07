package net.dzikoysk.funnyguilds.basic;

import java.util.UUID;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;
import net.dzikoysk.funnyguilds.util.ReflectionUtils;
import net.dzikoysk.funnyguilds.util.ScoreboardStack;
import net.dzikoysk.funnyguilds.util.element.IndividualPrefix;
import net.dzikoysk.funnyguilds.util.element.PlayerList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

public class User extends Object {
	
	private UUID uuid;
	private String name;
	private Guild guild;
	
	private long notification;
	private boolean enter;
	
	private long ban;
	private String reason;
	
	private BukkitTask teleportation;
	private Scoreboard scoreboard;
	private IndividualPrefix prefix;
	private PlayerList list;
	private Rank rank;
	
	private User(UUID uuid){
		this.uuid = uuid;
		UserUtils.addUser(this);
	}

	public void setName(String name){
		this.name = name;
	}
	
	public void setGuild(Guild guild){
		this.guild = guild;
	}
	
	public void setScoreboard(Scoreboard sb){
		this.scoreboard = sb;
	}
	
	public void setIndividualPrefix(IndividualPrefix prefix){
		this.prefix = prefix;
	}
	
	public void setPlayerList(PlayerList pl){
		this.list = pl;
	}
	
	public void setRank(Rank r){
		this.rank = r;
	}
	
	public void setBan(long l){
		this.ban = l;
	}
	
	public void setReason(String s){
		this.reason = s;
	}
	
	public void setNotificationTime(long time){
		this.notification = time;
	}
	
	public void setEnter(boolean b){
		this.enter = b;
	}
	
	public void setTeleportation(BukkitTask task){
		this.teleportation = task;
	}
	
	public UUID getUUID(){
		return this.uuid;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Guild getGuild(){
		return this.guild;
	}
	
	public synchronized Scoreboard getScoreboard(){
		if(this.scoreboard == null) this.scoreboard = ScoreboardStack.pull();
		return this.scoreboard;
	}
	
	public IndividualPrefix getIndividualPrefix(){
		if(this.prefix == null) new IndividualPrefix(this);
		return this.prefix;
	}
	
	public PlayerList getPlayerList(){
		if(this.list == null) new PlayerList(this);
		return this.list;
	}
	
	public Rank getRank(){
		if(this.rank != null) return this.rank;
		this.rank = new Rank(this);
		RankManager.getInstance().update(this);
		return this.rank;
	}
	
	public long getNotificationTime(){
		return this.notification;
	}
	
	public boolean getEnter(){
		return this.enter;
	}
	
	public BukkitTask getTeleportation(){
		return this.teleportation;
	}
	
	public long getBan(){
		return this.ban;
	}
	
	public String getReason(){
		if(this.reason != null) return ChatColor.translateAlternateColorCodes('&', this.reason);
		return "";
	}
	
	public Player getPlayer(){
		if(!isOnline()) return null;
		return Bukkit.getPlayer(this.name);
	}
	
	public int getPing(){
		int ping = 0;
		Player p = getPlayer();
		if(p == null) return ping;
		try {
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + ReflectionUtils.getVersion() + "entity.CraftPlayer");
			Object cp = craftPlayer.cast(p);
	        Object handle = craftPlayer.getMethod("getHandle").invoke(cp);
			ping = (int) handle.getClass().getField("ping").get(handle);
		} catch (ClassNotFoundException e) {
			FunnyGuilds.exception(e);
		} catch (NoSuchFieldException e) {
			FunnyGuilds.exception(e.getCause());
		} catch (Exception e) {
			FunnyGuilds.exception(e.getCause());
		}
		return ping;
	}
	
	public void removeGuild(){
		this.guild = null;
		IndependentThread.action(ActionType.RANK_UPDATE_USER, this);
	}
	
	public boolean hasGuild(){
		if(this.guild == null) return false;
		return true;
	}
	
	public boolean isOwner(){
		if(!hasGuild()) return false;
		if(this.guild.getOwner().equals(this)) return true;
		return false;
	}

	public boolean isOnline(){
		if(this.name == null) return false;
		Player p = Bukkit.getPlayer(this.name);
		if(p == null) return false;
		return true;
	}
	
	public boolean isBanned(){
		return this.ban != 0;
	}
	
	private User(String name){
		this(new OfflineUser(name));
	}
	
	private User(Player player){
		this.uuid = player.getUniqueId();
		this.name = player.getName();
		UserUtils.addUser(this);
	}
	
	private User(OfflineUser offline){
		this.uuid = UUID.fromString(offline.getUniqueId());
		this.name = offline.getName();
		UserUtils.addUser(this);
	}
	
	public static User get(UUID uuid){
		for(User lp : UserUtils.getUsers()) if(uuid.equals(lp.getUUID())) return lp;
		return new User(uuid);
	}

	public static User get(Player player){
		for(User u : UserUtils.getUsers()){
			if(u.getName() == null) continue;
			if(u.getName().equalsIgnoreCase(player.getName())) return u;
		}
		return new User(player);
	}
	
	public static User get(OfflinePlayer offline){
		for(User u : UserUtils.getUsers()){
			if(u.getName() == null) continue;
			if(u.getName().equalsIgnoreCase(offline.getName())) return u;
		}
		return new User(offline.getName());
	}
	
	public static User get(String name){
		for(User lp : UserUtils.getUsers()) if(name.equalsIgnoreCase(lp.getName())) return lp;
		return new User(name);
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
		if(o == null) return false;
		if(o.getClass() != this.getClass()) return false;
		User u = (User) o;
		if(u.getUUID() != this.uuid) return false;
		if(u.getName() != this.name) return false;
		return true;
	}

	@Override
	public String toString(){
		return this.name;
	}
}
