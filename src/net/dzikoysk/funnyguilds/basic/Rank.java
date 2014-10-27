package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.data.Config;

public class Rank implements Comparable<Rank> {
	
	private BasicType type;
	private Guild guild;
	private User user;
	private int points;
	private int kills;
	private int deaths;
	
	public Rank(User user){
		this.type = BasicType.USER;
		this.points = Config.getInstance().rankStart;
		this.user = user;
	}
	
	public Rank(Guild guild){
		this.type = BasicType.GUILD;
		this.guild = guild;
	}
	
	public void removePoints(int i){
		this.points -= i;
		if(this.points < 1) this.points = 0;
	}
	
	public void addPoints(int i){
		this.points += i;
	}
	
	public void addKill(){
		this.kills += 1;
	}
	
	public void addDeath(){
		this.deaths += 1;
	}
	
	public void setPoints(int i){
		this.points = i;
	}
	
	public void setKills(int i){
		this.kills = i;
	}
	
	public void setDeaths(int i){
		this.deaths = i;
	}
	
	public int getPoints(){
		if(this.type == BasicType.USER) return this.points;
		else {
			int points = 0;
			if(guild.getMembers().size() == 0) return 0;
			for(User user : guild.getMembers()) points += user.getRank().getPoints();
			this.points = points / guild.getMembers().size();
			return this.points;
		}
	}
	
	public int getKills(){
		return this.kills;
	}
	
	public int getDeaths(){
		return this.deaths;
	}
	
	public BasicType getType(){
		return type;
	}
	
	public User getUser(){
		return user;
	}
	
	public Guild getGuild(){
		return guild;
	}
	
	public String toString(){
		return Integer.toString(getPoints());
	}
	
	@Override
	public int compareTo(Rank rank) {
		int i = Integer.compare(rank.getPoints(), getPoints());
		if(i == 0){
			if(type == BasicType.USER && rank.getType() == BasicType.USER){
				if(user.getName().equals(rank.getUser().getName())) return 0;
				return user.getName().compareTo(rank.getUser().getName());	
			}
			else if(type == BasicType.GUILD && rank.getType() == BasicType.GUILD)
				if(guild.getName().equals(rank.getGuild().getName())) return 0;
				return guild.getName().compareTo(rank.getGuild().getName());
		}
		return i;
	}

}
