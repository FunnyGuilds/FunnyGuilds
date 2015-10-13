package net.dzikoysk.funnyguilds.system.validity;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.data.Messages;
import org.bukkit.Bukkit;

public class ValidityUtils {

    public static void broadcast(Guild guild) {
        if (guild == null || guild.getName() == null)
            return;
        Region region = guild.getRegion();
        String message = Messages.getInstance().getMessage("broadcastValidity")
                .replace("{GUILD}", guild.getName())
                .replace("{TAG}", guild.getTag())
                .replace("{GUILD}", guild.getName());
        if (region != null && region.getCenter() != null)
            message = message
                    .replace("{X}", Integer.toString(region.getCenter().getBlockX()))
                    .replace("{Y}", Integer.toString(region.getCenter().getBlockY()))
                    .replace("{Z}", Integer.toString(region.getCenter().getBlockZ()));
        else
            message = message
                    .replace("{X}", "Brak informacji")
                    .replace("{Y}", "Brak informacji")
                    .replace("{Z}", "Brak informacji");
        Bukkit.broadcastMessage(message);
    }

}
