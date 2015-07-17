package net.dzikoysk.funnyguilds.script;

import net.dzikoysk.funnyguilds.util.IOUtils;

import java.io.File;

public class ScriptManager {

	private static ScriptManager instance;

	public ScriptManager(){
		instance = this;
	}
	
	public void start(){
		File home = IOUtils.getFile("plugins/FunnyGuilds/scripts", true);
		File[] files = home.listFiles();
		if(files == null) return;
		for(File file : files){
			if(file.isDirectory()) continue;
			//if(file.getName().endsWith(".reflect"))
				//reflects.add(new PandaReflection(file));

		}
		run();
	}
	
	public void run(){

	}
	
	public static ScriptManager getInstance(){
		if(instance == null) new ScriptManager();
		return instance;
	}
	
}
