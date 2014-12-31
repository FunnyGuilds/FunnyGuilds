package net.dzikoysk.funnyguilds.util.element;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;

public class Dummy {
	
	private final User user;
	
	public Dummy(User user){
		this.user = user;
		this.initialize();
		this.user.setDummy(this);
	}

	public void updateScore(User user){
		Scoreboard scoreboard = this.user.getScoreboard();
		Objective objective = scoreboard.getObjective("points");
		if(objective == null){
			objective = scoreboard.registerNewObjective("points", "dummy");
			objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		}
		OfflineUser offline = user.getOfflineUser();
		objective.getScore(offline).setScore(user.getRank().getPoints());
	}
	
	private void initialize(){
		Scoreboard scoreboard = this.user.getScoreboard();
		Objective objective = scoreboard.getObjective("points");
		if(objective == null){
			objective = scoreboard.registerNewObjective("points", "dummy");
			objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		}
		for(Player player : Bukkit.getOnlinePlayers()){
			User user = User.get(player);
			Score score = objective.getScore(user.getOfflineUser());
			score.setScore(user.getRank().getPoints());
		}
	}
}
