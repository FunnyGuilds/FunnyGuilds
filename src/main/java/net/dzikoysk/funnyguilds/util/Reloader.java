package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.data.core.flat.Flat;
import org.bukkit.Bukkit;
import org.panda_lang.panda.util.configuration.PandaConfiguration;

import java.io.File;

public class Reloader {
	
	private static int actual;
	private static int before;
	private static boolean init;
	private static int reloadCount;
	private static boolean reloaded;
	
	public void init(){
		PandaConfiguration pc = new PandaConfiguration(new File(Flat.DATA, "funnyguilds.dat"));
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
