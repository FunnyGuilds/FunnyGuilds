package net.dzikoysk.funnyguilds.util;

import org.bukkit.scheduler.BukkitTask;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Config;

public class Repeater {

	private static Repeater instance;
	private volatile BukkitTask playerlist = null;

	public Repeater(){
		instance = this;
	}
	
	public void start() {
		if(this.playerlist == null)
			this.playerlist = FunnyGuilds.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(FunnyGuilds.getInstance(), new Runnable() {
				@Override
				public void run() {
					IndependentThread.action(ActionType.PLAYERLIST_GLOBAL_UPDATE);
				}
			}, 0, Config.getInstance().playerlistInterval*20);
	} 
	
	public void stop(){
		if(this.playerlist != null){
			this.playerlist.cancel();
			this.playerlist = null;
		}
	}
	
	public static Repeater getInstance(){
		if(instance == null) new Repeater();
		return instance;
	}

}
