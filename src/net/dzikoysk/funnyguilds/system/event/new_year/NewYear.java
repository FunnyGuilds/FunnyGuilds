package net.dzikoysk.funnyguilds.system.event.new_year;

import net.dzikoysk.funnyguilds.command.util.ExecutorCaller;
import net.dzikoysk.funnyguilds.system.event.EventExtension;

public class NewYear extends EventExtension {
	
	private static NewYear instance;
	private Thread thread;
	
	@Override
	public void onLoad(){
		instance = this;
		thread = Thread.currentThread();
	}
	
	@Override
	public void onEnable(){
		new NewYearTimer().start();
		new ExecutorCaller(new ExcFireworks(), "fajerwerki", "funnyguilds.admin", null);
	}
	
	public Thread getThread(){
		return thread;
	}
	
	public static NewYear getInstance(){
		return instance;
	}

}
