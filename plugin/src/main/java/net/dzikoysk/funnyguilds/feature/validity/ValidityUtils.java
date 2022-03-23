package net.dzikoysk.funnyguilds.feature.validity;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class ValidityUtils {

    private ValidityUtils() {
    }

    public static void broadcast(@Nullable Guild guild) {
        if (guild == null) {
            return;
        }

        String message = FunnyGuilds.getInstance().getMessageConfiguration().broadcastValidity
                .replace("{GUILD}", guild.getName())
                .replace("{TAG}", guild.getTag())
                .replace("{GUILD}", guild.getName());

        Option<Region> regionOption = guild.getRegion();
        if (guild.hasRegion() && regionOption.get().getCenter() != null) {
            Location center = regionOption.get().getCenter();
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
