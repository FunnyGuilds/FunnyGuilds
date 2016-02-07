package net.dzikoysk.funnyguilds;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.Commands;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.listener.AsyncPlayerChatListener;
import net.dzikoysk.funnyguilds.listener.EntityDamageListener;
import net.dzikoysk.funnyguilds.listener.PacketReceiveListener;
import net.dzikoysk.funnyguilds.listener.PlayerDeathListener;
import net.dzikoysk.funnyguilds.listener.PlayerInteractEntityListener;
import net.dzikoysk.funnyguilds.listener.PlayerJoinListener;
import net.dzikoysk.funnyguilds.listener.PlayerLoginListener;
import net.dzikoysk.funnyguilds.listener.PlayerQuitListener;
import net.dzikoysk.funnyguilds.listener.region.BlockBreakListener;
import net.dzikoysk.funnyguilds.listener.region.BlockIgniteListener;
import net.dzikoysk.funnyguilds.listener.region.BlockPhysicsListener;
import net.dzikoysk.funnyguilds.listener.region.BlockPlaceListener;
import net.dzikoysk.funnyguilds.listener.region.BucketActionListener;
import net.dzikoysk.funnyguilds.listener.region.EntityExplodeListener;
import net.dzikoysk.funnyguilds.listener.region.PlayerCommandListener;
import net.dzikoysk.funnyguilds.listener.region.PlayerInteractListener;
import net.dzikoysk.funnyguilds.listener.region.PlayerMoveListener;
import net.dzikoysk.funnyguilds.script.ScriptManager;
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
	}
	
	@Override
	public void onEnable(){
		new ScoreboardStack().start();
		new IndependentThread().start();
		new Manager().start();
		new AsynchronouslyRepeater().start();
		new Ticking().start();
		new MetricsCollector().start();
		new ScriptManager().start();

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PacketReceiveListener(), this);
		
		pm.registerEvents(new EntityDamageListener(), this);
		pm.registerEvents(new PlayerInteractEntityListener(), this);
		pm.registerEvents(new AsyncPlayerChatListener(), this);
		pm.registerEvents(new PlayerDeathListener(), this);
		pm.registerEvents(new PlayerJoinListener(), this);
		pm.registerEvents(new PlayerLoginListener(), this);
		pm.registerEvents(new PlayerQuitListener(), this);
		
		pm.registerEvents(new BlockBreakListener(), this);
		pm.registerEvents(new BlockIgniteListener(), this);
		pm.registerEvents(new BlockPlaceListener(), this);
		pm.registerEvents(new BucketActionListener(), this);
		pm.registerEvents(new EntityExplodeListener(), this);
		pm.registerEvents(new PlayerCommandListener(), this);
		pm.registerEvents(new PlayerInteractListener(), this);
		
		if(Settings.getInstance().eventMove) pm.registerEvents(new PlayerMoveListener(), this);
		if(Settings.getInstance().eventPhysics) pm.registerEvents(new BlockPhysicsListener(), this);
		
		patch();
		update();
		info("~ Created by & © Dzikoysk ~");
	} 
	
	@Override
	public void onDisable(){
		disabling = true;
		
		EntityUtil.despawn();
		PacketExtension.unregisterFunnyGuildsChannel();
		
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
	
	public static Player[] getOnlinePlayers(){
		Collection<? extends Player> collection = Bukkit.getOnlinePlayers();
		Player[] array = new Player[collection.size()];
		return collection.toArray(array);
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
		for (StackTraceElement st : ste) error("	at " + st.toString());
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
	
	public static File getFolder(){
		return funnyguilds.getDataFolder();
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
