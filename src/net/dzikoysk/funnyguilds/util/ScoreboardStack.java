package net.dzikoysk.funnyguilds.util;

import java.util.Stack;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardStack implements Runnable {
	
	private static ScoreboardStack instance;
	private static Stack<Scoreboard> stack = new Stack<>();
	
	public ScoreboardStack(){
		this.fill();
		instance = this;
	}
	
	@Override
	public void run(){
		fill();
	}
	
	public void start(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(FunnyGuilds.getInstance(), this, 0, 20);
	}
	
	private void fill(){
		int required = Bukkit.getMaxPlayers()*2;
		if(stack.size() < required){
			ScoreboardManager sm = Bukkit.getScoreboardManager();
			int loop = Bukkit.getMaxPlayers()*2 - stack.size();
			for(int i = 0; i < loop; i++) stack.push(sm.getNewScoreboard());
		}
	}
	
	public static Scoreboard pull(){
		return stack.pop();
	}

	public static int size(){
		return stack.size();
	}
	
	public static ScoreboardStack getInstance(){
		if(instance == null) new ScoreboardStack();
		return instance;
	}
}
