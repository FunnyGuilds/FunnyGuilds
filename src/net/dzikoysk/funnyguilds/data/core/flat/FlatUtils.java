package net.dzikoysk.funnyguilds.data.core.flat;

import java.io.File;

import net.dzikoysk.funnyguilds.FunnyGuilds;

public class FlatUtils {

	public static final File DATA = new File(FunnyGuilds.getFolder() + "data");
	public static final File USERS = new File(DATA + File.separator + "users");
	public static final File GUILDS = new File(DATA + File.separator + "guilds");
	public static final File REGIONS = new File(DATA + File.separator + "regions");
	
}
