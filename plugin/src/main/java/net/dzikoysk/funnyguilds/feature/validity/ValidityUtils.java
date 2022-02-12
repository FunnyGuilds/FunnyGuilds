package net.dzikoysk.funnyguilds.feature.validity;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import panda.std.Option;

public class ValidityUtils {

    private ValidityUtils() {}

    public static void broadcast(Guild guild) {
        if (guild == null || guild.getName() == null) {
            return;
        }

        String message = FunnyGuilds.getInstance().getMessageConfiguration().broadcastValidity
                .replace("{GUILD}", guild.getName())
                .replace("{TAG}", guild.getTag())
                .replace("{GUILD}", guild.getName());

        Option<Region> regionOption = guild.getRegionOption();
        if (guild.hasRegion() && regionOption.get().hasCenter()) {
            Region region = regionOption.get();
            Location center = region.getCenterOption().get();
            message = message
                    .replace("{X}", Integer.toString(center.getBlockX()))
                    .replace("{Y}", Integer.toString(center.getBlockY()))
                    .replace("{Z}", Integer.toString(center.getBlockZ()));
        }
        else {
            message = message
                    .replace("{X}", "Brak informacji")
                    .replace("{Y}", "Brak informacji")
                    .replace("{Z}", "Brak informacji");
        }

        Bukkit.broadcastMessage(message);
    }

}
