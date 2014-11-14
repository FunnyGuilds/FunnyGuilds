package net.dzikoysk.funnyguilds.util.configuration;

import java.io.File;
import java.util.Map;

public class PandaConfiguration {
	
	private final File configuration;
	private Map<String, Object> map;
	
	public PandaConfiguration(File file){
		this.configuration = file;
	}

	public Map<String, Object> getConfigurationMap(){
		return this.map;
	}
	
	public File getConfigurationFile(){
		return this.configuration;
	}
}
