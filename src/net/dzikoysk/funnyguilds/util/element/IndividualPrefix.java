package net.dzikoysk.funnyguilds.util.element;

import java.util.List;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.Config;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class IndividualPrefix {
	
	private final User user;
	
	public IndividualPrefix(User user){
		this.user = user;
		this.basic();
		user.setIndividualPrefix(this);
	}

	protected void addPlayer(Player player){
		if(player == null) return;
		User user = User.get(player);
		if(!user.hasGuild()) return;
		Scoreboard scoreboard = this.getUser().getScoreboard();
		Team team = scoreboard.getPlayerTeam(player);
		if(team != null) team.removePlayer(player);
		team = scoreboard.getTeam(user.getGuild().getTag());
		if(team == null){
			addGuild(user.getGuild());
			team = scoreboard.getTeam(user.getGuild().getTag());
		}
		if(this.getUser().hasGuild())
			if(this.getUser().equals(user) || this.getUser().getGuild().getMembers().contains(user))
				team.setPrefix(Config.getInstance().prefixOur
					.replace("{TAG}", user.getGuild().getTag()));
		team.addPlayer(player);
	}

	public void addGuild(Guild to){
		if(to == null) return;
		Scoreboard scoreboard = getUser().getScoreboard();
		Guild guild = getUser().getGuild();
		if(guild != null){
			if(guild.equals(to)){
				basic();
				return;
			}
			Team team = scoreboard.getTeam(to.getTag());
			if(team == null) team = scoreboard.registerNewTeam(to.getTag());
			for(User u : to.getMembers()){
				OfflinePlayer player = Bukkit.getOfflinePlayer(u.getName());
				if(!team.hasPlayer(player)) team.addPlayer(player);
			}
			String prefix = Config.getInstance().prefixOther;
			if(guild.getAllies().contains(to)) prefix = Config.getInstance().prefixAllies;
			if(guild.getEnemies().contains(to)) prefix = Config.getInstance().prefixEnemies;
			team.setPrefix(prefix.replace("{TAG}", to.getTag()));
		} else {
			Team team = scoreboard.getTeam(to.getTag());
			if(team == null) team = scoreboard.registerNewTeam(to.getTag());
			for(User u : to.getMembers()){
				OfflinePlayer player = Bukkit.getOfflinePlayer(u.getName());
				if(!team.hasPlayer(player)) team.addPlayer(player);
			}
			team.setPrefix(Config.getInstance().prefixOther.replace("{TAG}", to.getTag()));
		}
	}

	protected void removePlayer(OfflinePlayer op){
		if(op == null) return;
		Team team = getUser().getScoreboard().getPlayerTeam(op);
		if(team != null){
			team.removePlayer(op);
			if(team.getName() != null) team.setPrefix(Config.getInstance().prefixOther
					.replace("{TAG}", team.getName()));
		}
	}

	protected void removeGuild(Guild guild){
		if(guild == null) return;
		Team team = getUser().getScoreboard().getTeam(guild.getTag());
		if(team != null) team.unregister();
	}

	private void basic(){
		if(getUser() == null) return;
		List<Guild> guilds = GuildUtils.getGuilds();
		Scoreboard scoreboard = getUser().getScoreboard();
		Guild guild = getUser().getGuild();
		if(guild != null){
			guilds.remove(guild);
			Config config = Config.getInstance();
			String our = config.prefixOur;
			String ally = config.prefixAllies;
			String enemy = config.prefixEnemies;
			String other = config.prefixOther;
			Team team = scoreboard.getTeam(guild.getTag());
			if(team == null) team = scoreboard.registerNewTeam(guild.getTag());
			for(User u : guild.getMembers()){
				OfflinePlayer player = Bukkit.getOfflinePlayer(u.getName());
				if(!team.hasPlayer(player)) team.addPlayer(player);
			}
			team.setPrefix(our.replace("{TAG}", guild.getTag()));
			for(Guild one : guilds){
				if(one == null || one.getTag() == null) continue;
				team = scoreboard.getTeam(one.getTag());
				if(team == null) team = scoreboard.registerNewTeam(one.getTag());
				for(User u : one.getMembers()){
					OfflinePlayer player = Bukkit.getOfflinePlayer(u.getName());
					if(!team.hasPlayer(player)) team.addPlayer(player);
				}
				if(guild.getAllies().contains(one)) team.setPrefix(ally.replace("{TAG}", one.getTag()));
				else if(guild.getEnemies().contains(one)) team.setPrefix(enemy.replace("{TAG}", one.getTag()));
				else team.setPrefix(other.replace("{TAG}", one.getTag()));	
			}
		} else {
			String other = Config.getInstance().prefixOther;
			for(Guild one : guilds){
				if(one == null || one.getTag() == null) continue;
				Team team = scoreboard.getTeam(one.getTag());
				if(team == null) team = scoreboard.registerNewTeam(one.getTag());
				for(User u : one.getMembers()){
					OfflinePlayer player = Bukkit.getOfflinePlayer(u.getName());
					if(!team.hasPlayer(player)) team.addPlayer(player);
				}
				team.setPrefix(other.replace("{TAG}", one.getTag()));
			}
		}
	}
	
	public User getUser(){
		return user;
	}
	
	public Scoreboard getScoreboard(){
		return getUser().getScoreboard();
	}
}
