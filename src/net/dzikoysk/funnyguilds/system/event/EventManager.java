package net.dzikoysk.funnyguilds.system.event;

import java.util.ArrayList;
import java.util.List;

import net.dzikoysk.funnyguilds.system.event.christmas.Christmas;
import net.dzikoysk.funnyguilds.system.event.new_year.NewYear;

public class EventManager {

	private static EventManager instance;
	private List<EventExtension> extensions;
	private EventSettings settings;
	
	public EventManager(){
		instance = this;
		settings = EventSettings.getInstance();
		extensions = new ArrayList<>();
		
		if(settings.christmasEvent) extensions.add(new Christmas());
		if(settings.newYearEvent) extensions.add(new NewYear());
	}
	
	public final void load(){
		for(EventExtension ee : extensions) ee.onLoad();
	}
	
	public final void enable(){
		for(EventExtension ee : extensions) ee.onEnable();
	}
	
	public final void disable(){
		for(EventExtension ee : extensions) ee.onDisable();
	}
	
	public List<EventExtension> getExtensions(){
		return extensions;
	}
	
	public static EventManager getEventManager(){
		if(instance == null) new EventManager();
		return instance;
	}
}
