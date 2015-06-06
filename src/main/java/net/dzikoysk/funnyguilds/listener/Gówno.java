package net.dzikoysk.funnyguilds.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class Gówno implements Listener {
	private final String message = "[Spigot-Team] Skript was detected. Skript is not supported and may cause a server lags.";
	
	@EventHandler
	public void onGównoEnable(PluginEnableEvent e){
		if(e.getPlugin().getName().equals("Skript")){
			for(int i = 0; i < 10; i++) {
				System.out.println(this.message);
			}
		}
	}
	
	@EventHandler
	public void onGównoBroadcast(PlayerJoinEvent e){
		if(Bukkit.getPluginManager().isPluginEnabled("Skript") &&
				e.getPlayer().hasPermission("funnyguilds.admin")){
			for(int i = 0; i < 10; i++){
				e.getPlayer().sendMessage(this.message);
			}
		}
	}
}
