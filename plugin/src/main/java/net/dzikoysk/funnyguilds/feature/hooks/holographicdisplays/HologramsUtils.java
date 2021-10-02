package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.sections.HologramConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.Location;
import panda.std.Option;

public class HologramsUtils {

    private HologramsUtils() {}

    public static Option<Location> calculateLocation(Guild guild) {
        HologramConfiguration hologram = FunnyGuilds.getInstance().getPluginConfiguration().heartConfig.hologram;

        return guild.getCenter()
                .map(location -> location.add(hologram.locationCorrection.toLocation(location.getWorld())));
    }

}
