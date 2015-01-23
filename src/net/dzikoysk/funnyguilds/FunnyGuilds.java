package net.dzikoysk.funnyguilds;

import java.io.InputStream;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.Commands;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.listener.EntityDamage;
import net.dzikoysk.funnyguilds.listener.EntityInteract;
import net.dzikoysk.funnyguilds.listener.PacketReceive;
import net.dzikoysk.funnyguilds.listener.PlayerChat;
import net.dzikoysk.funnyguilds.listener.PlayerDeath;
import net.dzikoysk.funnyguilds.listener.PlayerJoin;
import net.dzikoysk.funnyguilds.listener.PlayerLogin;
import net.dzikoysk.funnyguilds.listener.PlayerQuit;
import net.dzikoysk.funnyguilds.listener.region.BlockBreak;
import net.dzikoysk.funnyguilds.listener.region.BlockIgnite;
import net.dzikoysk.funnyguilds.listener.region.BlockPhysics;
import net.dzikoysk.funnyguilds.listener.region.BlockPlace;
import net.dzikoysk.funnyguilds.listener.region.BucketAction;
import net.dzikoysk.funnyguilds.listener.region.EntityExplode;
import net.dzikoysk.funnyguilds.listener.region.PlayerCommand;
import net.dzikoysk.funnyguilds.listener.region.PlayerInteract;
import net.dzikoysk.funnyguilds.listener.region.PlayerMove;
import net.dzikoysk.funnyguilds.system.event.EventManager;
import net.dzikoysk.funnyguilds.util.IOUtils;
import net.dzikoysk.funnyguilds.util.Reloader;
import net.dzikoysk.funnyguilds.util.metrics.MetricsCollector;
import net.dzikoysk.funnyguilds.util.reflect.DescriptionChanger;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.reflect.PacketExtension;
import net.dzikoysk.funnyguilds.util.runnable.AsynchronouslyRepeater;
import net.dzikoysk.funnyguilds.util.runnable.ScoreboardStack;
import net.dzikoysk.funnyguilds.util.runnable.Ticking;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FunnyGuilds extends JavaPlugin {
	
	private static FunnyGuilds funnyguilds;
	private static Thread thread;
	private boolean disabling;
	
	public FunnyGuilds(){
		funnyguilds = this;
	}
	
	@Override
	public void onLoad(){
		thread = Thread.currentThread();
		
		new Reloader().init();
		new DescriptionChanger(getDescription()).name(Settings.getInstance().pluginName);
		new Commands().register();
		
		EventManager em = EventManager.getEventManager();
		em.load();
	}
	
	@Override
	public void onEnable(){
		new ScoreboardStack().start();
		new IndependentThread().start();
		new Manager().start();
		new AsynchronouslyRepeater().start();
		new Ticking().start();
		new MetricsCollector().start();
		
		EventManager em = EventManager.getEventManager();
		em.enable();

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PacketReceive(), this);
		
		pm.registerEvents(new EntityDamage(), this);
		pm.registerEvents(new EntityInteract(), this);
		pm.registerEvents(new PlayerChat(), this);
		pm.registerEvents(new PlayerDeath(), this);
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerLogin(), this);
		pm.registerEvents(new PlayerQuit(), this);
		
		pm.registerEvents(new BlockBreak(), this);
		pm.registerEvents(new BlockIgnite(), this);
		pm.registerEvents(new BlockPlace(), this);
		pm.registerEvents(new BucketAction(), this);
		pm.registerEvents(new EntityExplode(), this);
		pm.registerEvents(new PlayerCommand(), this);
		pm.registerEvents(new PlayerInteract(), this);
		
		if(Settings.getInstance().eventMove) pm.registerEvents(new PlayerMove(), this);
		if(Settings.getInstance().eventPhysics) pm.registerEvents(new BlockPhysics(), this);
		
		patch();
		update();
		info("~ Created by & © Dzikoysk ~");
	} 
	
	@Override
	public void onDisable(){
		disabling = true;
		
		EntityUtil.despawn();
		PacketExtension.unregisterFunnyGuildsChannel();
		EventManager.getEventManager().disable();
		
		AsynchronouslyRepeater.getInstance().stop();
		Manager.getInstance().stop();
		Manager.getInstance().save();
		
		funnyguilds = null;
	}
	
	private void update(){
		Thread thread = new Thread(){
			@Override
			public void run(){
				String latest = IOUtils.getContent("http://www.dzikoysk.net/projects/funnyguilds/latest.info");
				if(latest == null || latest.isEmpty()) update("Failed to check the new version of FunnyGuilds.");
				else if(latest.equalsIgnoreCase(getVersion())) update("You have a current version of FunnyGuilds.");
				else {
					update("");
					update("Available is new version of FunnyGuilds!");
					update("Current: " + getVersion());
					update("Latest: " + latest);
					update("");
				}
			}
		};
		thread.start();
	}
	
	private void patch(){
		for(final Player player : Bukkit.getOnlinePlayers()){
			Bukkit.getScheduler().runTask(this, new Runnable(){
				@Override
				public void run(){
					PacketExtension.registerPlayer(player);
				}
			});
			User user = User.get(player);
			user.getScoreboard();
			user.getDummy();
			user.getRank();
		}
		for(Guild guild : GuildUtils.getGuilds()){
			EntityUtil.spawn(guild);
			guild.updateRank();
		}
	}
	
	public static void update(String content){
		Bukkit.getLogger().info("[FunnyGuilds][Updater] > " + content);
	}
	
	public static void parser(String content){
		Bukkit.getLogger().severe("[FunnyGuilds][Parser] #> " + content);
	}
	
	public static void info(String content){
		Bukkit.getLogger().info("[FunnyGuilds] " + content);
	}
	
	public static void warning(String content){
		Bukkit.getLogger().warning("[FunnyGuilds] " + content);
	}
	
	public static void error(String content){
		Bukkit.getLogger().severe("[Server thread/ERROR] #!# " + content);
	}
	
	public static boolean exception(Throwable cause){
		if(cause == null) return true;
		return exception(cause.getMessage(), cause.getStackTrace());
	}
	
	public static boolean exception(String cause, StackTraceElement[] ste){
		error("");
		error("[FunnyGuilds] Severe error:");
	    error("");
	    error("Server Information:");
	    error("  FunnyGuilds: " + getVersion());
	    error("  Bukkit: " + Bukkit.getBukkitVersion());
	    error("  Java: " + System.getProperty("java.version"));
	    error("  Thread: " + Thread.currentThread());
	    error("  Running CraftBukkit: " + Bukkit.getServer().getClass().getName().equals("org.bukkit.craftbukkit.CraftServer"));
	    error("");
	    if (cause == null || ste == null || ste.length < 1) {
	    	error("Stack trace: no/empty exception given, dumping current stack trace instead!");
	    	return true;
	    } else error("Stack trace: ");
        error("Caused by: " + cause);
        for (StackTraceElement st : ste) error("    at " + st.toString());
        error("");
	    error("End of Error.");
	    error("");
	    return false;
	}
	
	@Override
	public InputStream getResource(String s) {
		return super.getResource(s);
	}
	
	public boolean isDisabling(){
		return disabling;
	}
	
	public static Thread getThread(){
		return thread;
	}
	
	public static String getVersion(){
		return funnyguilds.getDescription().getVersion();
	}
	
	public static FunnyGuilds getInstance(){
		if(funnyguilds == null) return new FunnyGuilds();
		return funnyguilds;
	}
}
