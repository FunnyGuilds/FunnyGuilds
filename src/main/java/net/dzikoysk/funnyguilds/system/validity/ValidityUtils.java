package net.dzikoysk.funnyguilds.system.validity;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import org.bukkit.Bukkit;

public class ValidityUtils {

    public static void broadcast(Guild guild) {
        if (guild == null || guild.getName() == null) {
            return;
        }

        String message = FunnyGuilds.getInstance().getMessageConfiguration().broadcastValidity
                .replace("{GUILD}", guild.getName())
                .replace("{TAG}", guild.getTag())
                .replace("{GUILD}", guild.getName());

        Region region = guild.getRegion();

        if (region != null && region.getCenter() != null) {
            message = message
                    .replace("{X}", Integer.toString(region.getCenter().getBlockX()))
                    .replace("{Y}", Integer.toString(region.getCenter().getBlockY()))
                    .replace("{Z}", Integer.toString(region.getCenter().getBlockZ()));
        } else {
            message = message
                    .replace("{X}", "Brak informacji")
                    .replace("{Y}", "Brak informacji")
                    .replace("{Z}", "Brak informacji");
        }
        
        Bukkit.broadcastMessage(message);
    }

}
