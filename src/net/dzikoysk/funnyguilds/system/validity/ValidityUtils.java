package net.dzikoysk.funnyguilds.system.validity;

import org.bukkit.Bukkit;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Messages;

public class ValidityUtils {
	
	public static void broadcast(Guild guild){
		if(guild == null || guild.getName() == null) return;
		Region region = RegionUtils.get(guild.getRegion());
		String message = Messages.getInstance().getMessage("broadcastValidity")
			.replace("{GUILD}", guild.getName())
			.replace("{TAG}", guild.getTag())
			.replace("{GUILD}", guild.getName());
		if(region != null) message = message
			.replace("{X}", Double.toString(region.getCenter().getX()))
			.replace("{Y}", Double.toString(region.getCenter().getY()))
			.replace("{Z}", Double.toString(region.getCenter().getZ()));
		Bukkit.broadcastMessage(message);
	}

}
