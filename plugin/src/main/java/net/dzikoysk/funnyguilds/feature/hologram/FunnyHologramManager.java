package net.dzikoysk.funnyguilds.feature.hologram;

import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.Location;
import panda.std.Option;

public interface FunnyHologramManager {

    Option<FunnyHologram> getOrCreateHologram(Guild guild);

    Option<FunnyHologram> deleteHologram(Guild guild);

    Option<Guild> deleteHologram(FunnyHologram hologram);

    Option<FunnyHologram> getFunnyHologram(Guild guild);

    Option<Location> getCorrectedLocation(Guild guild);

}
