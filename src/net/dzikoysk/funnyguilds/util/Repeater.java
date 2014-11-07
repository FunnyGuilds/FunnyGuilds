package net.dzikoysk.funnyguilds.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.system.ban.BanSystem;
import net.dzikoysk.funnyguilds.system.validity.ValiditySystem;

public class Repeater implements Runnable {

	private static Repeater instance;
	private volatile BukkitTask repeater;
	
	private int player_list;
	private int ban_system;
	private int validity_system;

	public Repeater(){
		instance = this;
	}
	
	public void start() {
		if(this.repeater != null) return;
		this.repeater = FunnyGuilds.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(FunnyGuilds.getInstance(), this, 0, 20);
	}
	
	@Override
	public void run() {
		player_list++;
		ban_system++;
		validity_system++;
		
		if(player_list == Config.getInstance().playerlistInterval) playerList();
		if(validity_system >= 5) validitySystem();
		if(ban_system >= 5) banSystem();
	}
	
	private void playerList(){
		if(Config.getInstance().playerlistEnable){
			IndependentThread.action(ActionType.PLAYERLIST_GLOBAL_UPDATE);
			for(Player p : Bukkit.getOnlinePlayers()) PacketUtils.sendPacket(p, PacketUtils.getPacket(p.getPlayerListName(), false, 0));
		}
		player_list = 0;
	}
	
	private void validitySystem(){
		ValiditySystem.getInstance().run();
		validity_system = 0;
	}
	
	private void banSystem(){
		BanSystem.getInstance().run();
		ban_system = 0;
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
