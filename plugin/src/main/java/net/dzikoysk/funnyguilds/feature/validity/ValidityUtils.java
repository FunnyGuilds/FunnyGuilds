package net.dzikoysk.funnyguilds.feature.validity;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public final class ValidityUtils {

    private ValidityUtils() {
    }

    public static void broadcast(@Nullable Guild guild) {
        if (guild == null) {
            return;
        }

        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{GUILD}", guild.getName());

        Option<Region> regionOption = guild.getRegion();
        if (regionOption.isPresent() && regionOption.get().getCenter() != null) {
            Location center = regionOption.get().getCenter();

            formatter.register("{X}", center.getBlockX());
            formatter.register("{Y}", center.getBlockY());
            formatter.register("{Z}", center.getBlockZ());
        }
        else {
            formatter.register("{X}", messages.noInformation);
            formatter.register("{Y}", messages.noInformation);
            formatter.register("{Z}", messages.noInformation);
        }

        Bukkit.broadcastMessage(formatter.format(messages.broadcastValidity));
    }

}
