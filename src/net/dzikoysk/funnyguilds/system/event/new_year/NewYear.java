package net.dzikoysk.funnyguilds.system.event.new_year;

import net.dzikoysk.funnyguilds.system.event.EventExtension;

public class NewYear extends EventExtension {
	
	private static NewYear instance;
	
	@Override
	public void onLoad(){
		instance = this;
	}
	
	public static NewYear getInstance(){
		return instance;
	}

}
