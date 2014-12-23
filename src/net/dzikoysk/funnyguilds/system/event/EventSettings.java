package net.dzikoysk.funnyguilds.system.event;

import java.io.File;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.util.configuration.PandaConfiguration;

public class EventSettings {
	
	private static final File FILE = new File(FunnyGuilds.getInstance().getDataFolder(), "events.yml");
	private static EventSettings instance;
	
	public boolean christmasEvent;
	public boolean newYearEvent;
	
	public EventSettings(){
		instance = this;
		load();
	}
	
	private void load(){
		Manager.loadDefaultFiles(new String[] { "events.yml" });
		PandaConfiguration pc = new PandaConfiguration(FILE);
		
		christmasEvent = pc.getBoolean("christmas-event");
	}

	public static EventSettings getInstance(){
		if(instance == null) new EventSettings();
		return instance;
	}
}
