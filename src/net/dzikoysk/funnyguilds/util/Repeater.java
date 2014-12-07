package net.dzikoysk.funnyguilds.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.system.ban.BanSystem;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.system.validity.ValiditySystem;

public class Repeater implements Runnable {

	private static Repeater instance;
	private volatile BukkitTask repeater;
	
	private int player_list;
	private int ban_system;
	private int validity_system;
	private int protection_system;
	
	private int player_list_time;

	public Repeater(){
		instance = this;
		player_list_time = Settings.getInstance().playerlistInterval;
	}
	
	public void start() {
		if(this.repeater != null) return;
		this.repeater = Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), this, 0, 20);
	}
	
	@Override
	public void run() {
		player_list++;
		ban_system++;
		validity_system++;
		protection_system++;
		
		if(player_list == player_list_time) playerList();
		if(validity_system >= 10) validitySystem();
		if(ban_system >= 7) banSystem();
		if(protection_system >= 20) protectionSystem();
	}
	
	private void playerList(){
		if(Settings.getInstance().playerlistEnable){
			IndependentThread.action(ActionType.PLAYERLIST_GLOBAL_UPDATE);
			if(Settings.getInstance().playerlistPatch)
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
	
	private void protectionSystem(){
		for(Guild guild : GuildUtils.getGuilds()) ProtectionSystem.respawn(guild);
		protection_system = 0;
	}
	
	public void reload(){
		player_list_time = Settings.getInstance().playerlistInterval;
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
