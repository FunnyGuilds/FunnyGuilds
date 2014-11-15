package net.dzikoysk.funnyguilds.util.configuration;

public class ConfigurationObject {
	
	private final Object object;
	private final ConfigurationType type;
	
	public ConfigurationObject(Object object, ConfigurationType type){
		this.object = object;
		this.type = type;
	}

	public Object getObject(){
		return this.object;
	}
	
	public ConfigurationType getType(){
		return this.type;
	}
}
