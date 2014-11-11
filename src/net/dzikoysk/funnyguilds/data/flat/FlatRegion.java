package net.dzikoysk.funnyguilds.data.flat;

import java.io.File;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.DataManager;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.YamlFactor;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

public class FlatRegion {
	
	private final Region region;
	
	public FlatRegion(Region region){
		this.region = region;
	}
	
	public boolean serialize() {
		File file = DataManager.loadCustomFile(BasicType.REGION, region.getName());
		YamlFactor yml = new YamlFactor(file);
		YamlConfiguration yaml = yml.getParent();
		yaml.set("name", region.getName());
		yaml.set("center", Parser.toString(region.getCenter()));
		yaml.set("size", region.getSize());
		yaml.set("enlarge", region.getEnlarge());
		yml.close();
		return true;
	}
	
	public static Region deserialize(File file){
		YamlFactor yaml = new YamlFactor(file);

		Object[] values = new Object[4];
		String name = yaml.getParent().getString("name");
		String cs = yaml.getParent().getString("center");
		int size = yaml.getParent().getInt("size");
		int enlarge = yaml.getParent().getInt("enlarge");
		
		if(name == null || cs == null){
			FunnyGuilds.error("Cannot deserialize region! Caused by: name/center is null");
			return null;
		}
		
		Location center = Parser.parseLocation(cs);
		if(center == null){
			FunnyGuilds.error("Cannot deserialize region! Caused by: center is null");
			return null;
		}
		if(size < 1) size = Settings.getInstance().regionSize;
			
		values[0] = name;
		values[1] = center;
		values[2] = size;
		values[3] = enlarge;
		return DeserializationUtils.deserializeRegion(values);
	}

}
