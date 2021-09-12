package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import net.dzikoysk.funnyguilds.guild.Guild;
import panda.std.Option;

public interface FunnyHologramManager {

    Option<FunnyHologram> createHologram(Guild guild);

    void deleteHologram(Guild guild);

    void deleteHologram(FunnyHologram hologram);

    Option<FunnyHologram> getFunnyHologram(Guild guild);

}
