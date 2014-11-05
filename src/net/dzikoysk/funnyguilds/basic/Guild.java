package net.dzikoysk.funnyguilds.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;

import org.bukkit.Location;

public class Guild {

	private UUID uuid;
	private String name;
	private String tag;
	private User owner;
	private Rank rank;
	private String region;
	private Location home;
	private List<User> members = new ArrayList<>();
	private List<String> regions = new ArrayList<>();
	private List<Guild> allies = new ArrayList<>();
	private List<Guild> enemies = new ArrayList<>();
	private long born;
	private long validity;
	private long attacked;
	private long ban;
	private int lives;
	private long build;
	
	private Guild(UUID uuid){
		this.uuid = uuid;
		GuildUtils.addGuild(this);
	}
	
	public Guild(String name){
		this.uuid = UUID.randomUUID();
		this.name = name;
		GuildUtils.addGuild(this);
	}
	
	public void setUUID(UUID uuid){
		this.uuid = uuid;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public void setTag(String tag){
		this.tag = tag;
	}
	
	public void setOwner(User user){
		this.owner = user;
		this.members.add(user);
	}
	
	public void setRank(Rank rank){
		this.rank = rank;
	}
	
	public void setRegion(String s){
		this.region = s;
		if(this.home == null){
			Region region = Region.get(s);
			this.home = region.getCenter();
		}
	}
	
	public void setHome(Location home){
		this.home = home;
	}
	
	public void setMembers(List<User> members){
		this.members = members;
	}
	
	public void setRegions(List<String> regions){
		this.regions = regions;
	}
	
	public void setAllies(List<Guild> guilds){
		this.allies = guilds;
	}
	
	public void setEnemies(List<Guild> guilds){
		this.enemies = guilds;
	}

	public void setBorn(long l){
		this.born = l;
	}
	
	public void setValidity(long l){
		this.validity = l;
	}
	
	public void setAttacked(long l){
		this.attacked = l;
	}
	
	public void setLives(int i){
		this.lives = i;
	}
	
	public void setBuild(long time){
		this.build = time;
	}
	
	public void setBan(long l){
		if(l > System.currentTimeMillis()) this.ban = l;
		this.ban = 0;
	}
	
	public void addMember(User user){
		if(this.members.contains(user)) return;
		this.members.add(user);
		this.getRank();
	}
	
	public void addRegion(String s){
		if(this.regions.contains(s)) return;
		this.regions.add(s);
	}
	
	public void addAlly(Guild guild){
		if(this.allies.contains(guild)) return;
		this.allies.add(guild);
	}
	
	public void addEnemy(Guild guild){
		if(this.enemies.contains(guild)) return;
		this.enemies.add(guild);
	}
	
	public void deserializationUpdate() {
		this.owner.setGuild(this);
		UserUtils.setGuild(this.members, this);
		for(String r : this.regions){
			Region region = RegionUtils.get(r);
			if(region != null) region.setGuild(this);
		}
	}

	public void removeMember(User user){
		this.members.remove(user);
	}
	
	public void removeAlly(Guild guild){
		this.allies.remove(guild);
	}
	
	public void removeEnemy(Guild guild){
		this.enemies.remove(guild);
	}
	
	public void delete(){
		GuildUtils.removeGuild(this);
		this.uuid = null;
		this.name = null;
	}
	
	public boolean isBanned(){
		if(this.ban > System.currentTimeMillis()) return true;
		this.ban = 0;
		return false;
	}
	
	public boolean canBuild(){
		if(this.build > System.currentTimeMillis()) return false;
		this.build = 0;
		return true;
	}
	
	public UUID getUUID(){
		return this.uuid;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getTag(){
		return this.tag;
	}
	
	public User getOwner(){
		return this.owner;
	}
	
	public String getRegion(){
		return this.region;
	}
	
	public Location getHome(){
		return this.home;
	}
	
	public List<User> getMembers(){
		return this.members;
	}
	
	public List<String> getRegions(){
		return this.regions;
	}
	
	public List<Guild> getAllies(){
		return this.allies;
	}
	
	public List<Guild> getEnemies(){
		return this.enemies;
	}
	
	public long getBorn(){
		return this.born;
	}
	
	public long getValidity(){
		return this.validity;
	}
	
	public long getAttacked(){
		return this.attacked;
	}
	
	public int getLives(){
		return this.lives;
	}
	
	public long getBan(){
		return this.ban;
	}
	
	public long getBuild(){
		return this.build;
	}
	
	public Rank getRank(){
		if(this.rank != null) return this.rank;
		this.rank = new Rank(this);
		RankManager.getInstance().update(this);
		return this.rank;
	}
	
	public static Guild get(UUID uuid){
		for(Guild guild : GuildUtils.getGuilds()) if(guild.getUUID().equals(uuid)) return guild;
		return new Guild(uuid);
	}
	
	public static Guild get(String name){
		for(Guild guild : GuildUtils.getGuilds()) if(guild.getName().equalsIgnoreCase(name)) return guild;
		return new Guild(name);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o){
		if(o == null) return false;
		if(o.getClass() != this.getClass()) return false;
		if(!((Guild)o).getUUID().equals(this.uuid)) return false;
		return true;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
}
