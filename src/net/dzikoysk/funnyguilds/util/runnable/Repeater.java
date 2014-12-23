package net.dzikoysk.funnyguilds.util.runnable;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class Repeater implements Runnable {

	private static Repeater instance;
	private volatile BukkitTask repeater;
	
	private int protection_system;
	
	public Repeater(){
		instance = this;
	}
	
	public void start() {
		if(this.repeater != null) return;
		this.repeater = Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), this, 100, 20);
	}
	
	@Override
	public void run() {
		protection_system++;
		if(protection_system >= 60) protectionSystem();
	}
	
	private void protectionSystem(){
		if(Settings.getInstance().createStringMaterial.equalsIgnoreCase("ender crystal"))
			for(Guild guild : GuildUtils.getGuilds()) ProtectionSystem.respawn(guild);
		protection_system = 0;
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
