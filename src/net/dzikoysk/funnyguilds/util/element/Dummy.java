package net.dzikoysk.funnyguilds.util.element;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Settings;

public class Dummy {
	
	private final User user;
	
	public Dummy(User user){
		this.user = user;
		this.user.setDummy(this);
		this.initialize();
	}

	public void updateScore(User user){
		Scoreboard scoreboard = this.user.getScoreboard();
		Objective objective = scoreboard.getObjective("points");
		if(objective == null) objective = initialize();
		OfflineUser offline = user.getOfflineUser();
		objective.getScore(offline).setScore(user.getRank().getPoints());
	}
	
	private Objective initialize(){
		Scoreboard scoreboard = this.user.getScoreboard();
		Objective objective = scoreboard.getObjective("points");
		if(objective == null){
			objective = scoreboard.registerNewObjective("points", "dummy");
			objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
			objective.setDisplayName(Settings.getInstance().dummySuffix);
		}
		for(Player player : Bukkit.getOnlinePlayers()){
			User user = User.get(player);
			Score score = objective.getScore(user.getOfflineUser());
			score.setScore(user.getRank().getPoints());
		}
		return objective;
	}
}
