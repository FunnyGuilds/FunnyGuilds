package net.dzikoysk.funnyguilds.system.event.christmas;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ChristmasTask implements Runnable {
	
	private static final long TIME = 20L*60L*10L;
	private static ChristmasTask instance;
	private BukkitTask task;
	
	public ChristmasTask(){
		instance = this;
	}
	
	public void start(){
		task = Bukkit.getScheduler().runTaskTimerAsynchronously(FunnyGuilds.getInstance(), this, TIME, TIME);
	}
	
	public void stop(){
		if(task != null) task.cancel();
		task = null;
	}
	
	@Override
	public void run(){
		ChristmasData.getInstance().save();
	}
	
	public static ChristmasTask getInstance(){
		return instance;
	}
}
