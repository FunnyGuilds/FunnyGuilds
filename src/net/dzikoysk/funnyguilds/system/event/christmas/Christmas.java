package net.dzikoysk.funnyguilds.system.event.christmas;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.command.util.ExecutorCaller;
import net.dzikoysk.funnyguilds.system.event.EventExtension;

public class Christmas extends EventExtension {
	
	private static Christmas instance;
	private Thread thread;

	@Override
	public void onLoad(){
		instance = this;
		thread = Thread.currentThread();
	}
	
	@Override
	public void onEnable(){
		new ChristmasGift();
		new ChristmasStats();
		new ChristmasData().load();
		new ChristmasRecipe().register();
		new ChristmasTask().start();
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new ChristmasListener(), FunnyGuilds.getInstance());
		
		new ExecutorCaller(new ExcChristmasTree(), "choinka", null, null);
		new ExecutorCaller(new ExcPresents(), "prezenty", null, null);
		new ExecutorCaller(new ExcPresent(), "prezent", null, null);
	}
	
	@Override
	public void onDisable(){
		ChristmasTask.getInstance().stop();
		ChristmasData.getInstance().save();
	}
	
	public static String getPrefix(){
		return "&f[&cSwiety Mikolaj&f] ";
	}
	
	public Thread getThread(){
		return thread;
	}
	
	public static Christmas getInstance(){
		return instance;
	}
	
}
