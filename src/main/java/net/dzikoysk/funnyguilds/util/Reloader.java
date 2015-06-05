package net.dzikoysk.funnyguilds.util;

import java.io.File;

import net.dzikoysk.funnyguilds.data.Data;
import net.dzikoysk.panda.util.configuration.PandaConfiguration;

import org.bukkit.Bukkit;

public class Reloader {
	
	private static int actual;
	private static int before;
	private static boolean init;
	private static int reloadCount;
	private static boolean reloaded;
	
	public void init(){
		PandaConfiguration pc = new PandaConfiguration(new File(Data.getDataFolder(), "funnyguilds.dat"));
		before = pc.getInt("played-before");
		actual = Bukkit.getOnlinePlayers().size();
		reloaded = before == actual;
		if(reloaded) reloadCount = pc.getInt("reload-count") + 1;
		init = true;
	}
	
	public static boolean wasReloaded(){
		if(!init) new Reloader().init();
		return reloaded;
	}
	
	public static int getReloadCount(){
		return reloadCount;
	}

}
