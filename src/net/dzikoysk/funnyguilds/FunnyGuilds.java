package net.dzikoysk.funnyguilds;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.ExcAlly;
import net.dzikoysk.funnyguilds.command.ExcBase;
import net.dzikoysk.funnyguilds.command.ExcBreak;
import net.dzikoysk.funnyguilds.command.ExcConfirm;
import net.dzikoysk.funnyguilds.command.ExcCreate;
import net.dzikoysk.funnyguilds.command.ExcDelete;
import net.dzikoysk.funnyguilds.command.ExcEnlarge;
import net.dzikoysk.funnyguilds.command.ExcFunnyGuilds;
import net.dzikoysk.funnyguilds.command.ExcGuild;
import net.dzikoysk.funnyguilds.command.ExcInfo;
import net.dzikoysk.funnyguilds.command.ExcInvite;
import net.dzikoysk.funnyguilds.command.ExcJoin;
import net.dzikoysk.funnyguilds.command.ExcKick;
import net.dzikoysk.funnyguilds.command.ExcLeave;
import net.dzikoysk.funnyguilds.command.ExcPlayer;
import net.dzikoysk.funnyguilds.command.ExcTop;
import net.dzikoysk.funnyguilds.command.ExcValidity;
import net.dzikoysk.funnyguilds.command.admin.AxcAdd;
import net.dzikoysk.funnyguilds.command.admin.AxcBan;
import net.dzikoysk.funnyguilds.command.admin.AxcDeaths;
import net.dzikoysk.funnyguilds.command.admin.AxcDelete;
import net.dzikoysk.funnyguilds.command.admin.AxcMain;
import net.dzikoysk.funnyguilds.command.admin.AxcKick;
import net.dzikoysk.funnyguilds.command.admin.AxcKills;
import net.dzikoysk.funnyguilds.command.admin.AxcPoints;
import net.dzikoysk.funnyguilds.command.admin.AxcTeleport;
import net.dzikoysk.funnyguilds.command.util.ExecutorCaller;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.DataManager;
import net.dzikoysk.funnyguilds.listener.PlayerChat;
import net.dzikoysk.funnyguilds.listener.EntityDamage;
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
import net.dzikoysk.funnyguilds.listener.region.PlayerInteract;
import net.dzikoysk.funnyguilds.listener.region.PlayerMove;
import net.dzikoysk.funnyguilds.util.IOUtils;
import net.dzikoysk.funnyguilds.util.IndependentThread;
import net.dzikoysk.funnyguilds.util.Metrics;
import net.dzikoysk.funnyguilds.util.Metrics.Graph;
import net.dzikoysk.funnyguilds.util.Repeater;
import net.dzikoysk.funnyguilds.util.ScoreboardStack;
import net.dzikoysk.funnyguilds.util.Ticking;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FunnyGuilds extends JavaPlugin {
	
	private static Plugin funnyguilds;
	private static Thread thread;
	
	@Override
	public void onEnable(){
				
		new ScoreboardStack().start();
		new IndependentThread().start();
		
		new DataManager();
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new EntityDamage(), this);
		pm.registerEvents(new PlayerChat(), this);
		pm.registerEvents(new PlayerDeath(), this);
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerLogin(), this);
		pm.registerEvents(new PlayerQuit(), this);
		
		pm.registerEvents(new BlockBreak(), this);
		pm.registerEvents(new BlockIgnite(), this);
		pm.registerEvents(new BlockPhysics(), this);
		pm.registerEvents(new BlockPlace(), this);
		pm.registerEvents(new BucketAction(), this);
		pm.registerEvents(new EntityExplode(), this);
		pm.registerEvents(new PlayerInteract(), this);
		pm.registerEvents(new PlayerMove(), this);
		
		this.start();
		new Ticking().start();
		new Repeater().start();
		this.patch();
		info("~ Created by & © Dzikoysk ~");
	} 
	
	@Override
	public void onLoad(){
		
		thread = Thread.currentThread();
		funnyguilds = this;
		
		DataManager.loadDefaultFiles(new String[] { "messages.yml", "config.yml" });
		Config c = Config.getInstance();
		
		new ExecutorCaller(new ExcFunnyGuilds(), "funnyguilds", null, null);
		new ExecutorCaller(new ExcCreate(), c.excCreate, "funnyguilds.create", c.excCreateAliases);
		new ExecutorCaller(new ExcDelete(), c.excDelete, "funnyguilds.delete", c.excDeleteAliases);
		new ExecutorCaller(new ExcConfirm(), c.excConfirm, "funnyguilds.delete", c.excConfirmAliases);
		new ExecutorCaller(new ExcInvite(), c.excInvite, "funnyguilds.invite", c.excInviteAliases);
		new ExecutorCaller(new ExcJoin(), c.excJoin, "funnyguilds.join", c.excJoinAliases);
		new ExecutorCaller(new ExcLeave(), c.excLeave, "funnyguilds.leave", c.excLeaveAliases);
		new ExecutorCaller(new ExcKick(), c.excKick, "funnyguilds.kick", c.excKickAliases);
		new ExecutorCaller(new ExcBase(), c.excBase, "funnyguilds.base", c.excBaseAliases);
		new ExecutorCaller(new ExcEnlarge(), c.excEnlarge, "funnyguilds.enlarge", c.excEnlargeAliases);
		new ExecutorCaller(new ExcGuild(), c.excGuild, "funnyguilds.help", c.excGuildAliases);
		new ExecutorCaller(new ExcAlly(), c.excAlly, "funnyguilds.ally", c.excAllyAliases);
		new ExecutorCaller(new ExcBreak(), c.excBreak, "funnyguilds.break", c.excBreakAliases);
		new ExecutorCaller(new ExcPlayer(), c.excPlayer, "funnyguilds.player", c.excPlayerAliases);
		new ExecutorCaller(new ExcInfo(), c.excInfo, "funnyguilds.info", c.excInfoAliases);
		new ExecutorCaller(new ExcTop(), c.excTop, "funnyguilds.top", c.excTopAliases);
		new ExecutorCaller(new ExcValidity(), c.excValidity, "funnyguilds.validity", c.excValidityAliases);
		
		new ExecutorCaller(new AxcMain(), c.axcMain, "funnyguilds.admin", null);
		new ExecutorCaller(new AxcAdd(), c.axcAdd, "funnyguilds.admin", null);
		new ExecutorCaller(new AxcDelete(), c.axcDelete, "funnyguilds.admin", null);
		new ExecutorCaller(new AxcKick(), c.axcKick, "funnyguilds.admin", null);
		new ExecutorCaller(new AxcTeleport(), c.axcTeleport, "funnyguilds.admin", null);
		new ExecutorCaller(new AxcPoints(), c.axcPoints, "funnyguilds.admin", null);
		new ExecutorCaller(new AxcKills(), c.axcKills, "funnyguilds.admin", null);
		new ExecutorCaller(new AxcDeaths(), c.axcDeaths, "funnyguilds.admin", null);
		new ExecutorCaller(new AxcBan(), c.axcBan, "funnyguilds.admin", null);
	}
	
	@Override
	public void onDisable(){
		Repeater.getInstance().stop();
		DataManager.getInstance().stop();
		DataManager.getInstance().save();
		funnyguilds = null;
	}
	
	private void metrics(){
		try {
		    Metrics metrics = new Metrics(this);
		    Graph global = metrics.createGraph("Guilds and Users");
		    global.addPlotter(new Metrics.Plotter("Guilds"){
				@Override
				public int getValue() {
					return GuildUtils.getGuilds().size();
				}
		    });
		    global.addPlotter(new Metrics.Plotter("Users"){
				@Override
				public int getValue() {
					return UserUtils.getUsers().size();
				}
		    });
		    metrics.addGraph(global);
		    metrics.start();
		} catch (Exception e) {
			error("[FunnyGuilds][Metrics] " + e.getMessage());
		}
	}
	
	private void thread(){
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
	
	private void start(){
		metrics();
		thread();
	}
	
	private void patch(){
		for(Player p : Bukkit.getOnlinePlayers()){
			User user = User.get(p);
			user.getScoreboard();
			user.getRank();
		}
		for(Guild guild : GuildUtils.getGuilds())
			guild.getRank();
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
	
	public static String getVersion(){
		return funnyguilds.getDescription().getVersion();
	}
	
	public static Thread getThread(){
		return thread;
	}
	
	public static Plugin getInstance(){
		return funnyguilds;
	}
}
