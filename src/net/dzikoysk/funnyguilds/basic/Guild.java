package net.dzikoysk.funnyguilds.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.util.Parser;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

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
	
	public void setBuild(long time){
		this.build = time;
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
	
	public YamlConfiguration serialize(YamlConfiguration yaml) {
		if(this.name == null){
			FunnyGuilds.error("[Serialize] Cannot serialize guild! Caused by: name is null");
			return null;
		}else if(this.tag == null){
			FunnyGuilds.error("[Serialize] Cannot serialize guild: " + this.name + "! Caused by: tag is null");
			return null;
		}else if(this.owner == null){
			FunnyGuilds.error("[Serialize] Cannot serialize guild: " + this.name + "! Caused by: owner is null");
			return null;
		}else if(this.region == null){
			FunnyGuilds.error("[Serialize] Cannot serialize guild: " + this.name + "! Caused by: region is null");
			return null;
		}else if(this.uuid == null) this.uuid = UUID.randomUUID();
		
		yaml.set("uuid", this.uuid.toString());
		yaml.set("name", this.name);
		yaml.set("tag", this.tag);
		yaml.set("owner", this.owner.getName());
		yaml.set("home", Parser.toString(this.home));
		yaml.set("members", UserUtils.getNames(this.members));
		yaml.set("region", this.region);
		yaml.set("regions", this.regions);
		yaml.set("allies", GuildUtils.getNames(this.allies));
		yaml.set("enemies", GuildUtils.getNames(this.enemies));
		return yaml;
	}
	
	@SuppressWarnings("unchecked")
	public static Guild deserialize(Object[] values){
		if(values == null){
			FunnyGuilds.error("[Deserialize] Cannot deserialize guild! Caused by: null");
			return null;
		}
		Guild guild = Guild.get((String) values[1]);
		guild.setUUID((UUID) values[0]);
		guild.setTag((String) values[2]);
		guild.setOwner((User) values[3]);
		guild.setHome((Location) values[4]);
		guild.setRegion((String) values[5]);
		guild.setMembers((List<User>) values[6]);
		guild.setRegions((List<String>) values[7]);
		guild.setAllies((List<Guild>) values[8]);
		guild.setEnemies((List<Guild>) values[9]);
		guild.deserializationUpdate();
		return guild;
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
	
	public Rank getRank(){
		if(this.rank == null){
			this.rank = new Rank(this);
			RankManager.getInstance().update(this);
		}
		return this.rank;
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
	
	public long getBuild(){
		return this.build;
	}
	
	public static Guild get(UUID uuid){
		for(Guild guild : GuildUtils.getGuilds()){
			if(guild.getUUID().equals(uuid)) return guild;
		}
		return new Guild(uuid);
	}
	
	public static Guild get(String name){
		for(Guild guild : GuildUtils.getGuilds()){
			if(guild.getName().equalsIgnoreCase(name)) return guild;
		}
		return new Guild(name);
	}
}
