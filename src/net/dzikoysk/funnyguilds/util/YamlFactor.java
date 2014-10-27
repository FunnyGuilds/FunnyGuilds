package net.dzikoysk.funnyguilds.util;

import java.io.File;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlFactor {
	
	private final File file;
	private YamlConfiguration yaml;
	
	public YamlFactor(String path){
		this.file = new File(path.replace("/", File.separator));
		if(!this.file.exists()) try {
			this.file.getParentFile().mkdirs();
			this.file.createNewFile();
		} catch (Exception e) {
			FunnyGuilds.exception(e);
		}
	}
	
	public YamlFactor(File file){
		this.file = file;
		if(!this.file.exists()) try {
			this.file.getParentFile().mkdirs();
			this.file.createNewFile();
		} catch (Exception e) {
			FunnyGuilds.exception(e);
		}
	}

	public YamlFactor open(){
		yaml = YamlConfiguration.loadConfiguration(file);
		return this;
	}
	
	public YamlFactor close(){
		if(yaml != null) try {
			yaml.save(file);
		} catch (Exception e) {
			FunnyGuilds.exception(e);
		}
		clear();
		return this;
	}
	
	public YamlFactor clear(){
		yaml = null;
		return this;
	}
	
	public YamlConfiguration getParent(){
		if(yaml == null) open();
		return yaml;
	}
	
	public YamlConfiguration getCurrentParent(){
		return yaml;
	}
}
