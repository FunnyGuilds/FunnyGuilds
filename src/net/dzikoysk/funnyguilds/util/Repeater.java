package net.dzikoysk.funnyguilds.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.system.ban.BanManager;

public class Repeater implements Runnable {

	private static Repeater instance;
	private volatile BukkitTask repeater;
	
	private int playerlist;

	public Repeater(){
		instance = this;
	}
	
	public void start() {
		if(this.repeater != null) return;
		this.repeater = FunnyGuilds.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(FunnyGuilds.getInstance(), this, 0, 20);
	}
	
	@Override
	public void run() {
		BanManager.getInstance().run();
		if(playerlist == Config.getInstance().playerlistInterval) playerList();
		playerlist++;
	}
	
	private void playerList(){
		if(Config.getInstance().playerlistEnable){
			IndependentThread.action(ActionType.PLAYERLIST_GLOBAL_UPDATE);
			for(Player p : Bukkit.getOnlinePlayers()) PacketUtils.sendPacket(p, PacketUtils.getPacket(p.getPlayerListName(), false, 0));
		}
		playerlist = 0;
	}
	
	public void stop(){
		if(repeater != null){
			repeater.cancel();
			repeater = null;
		}
	}
	
	public static Repeater getInstance(){
		if(instance == null) new Repeater();
		return instance;
	}

}
