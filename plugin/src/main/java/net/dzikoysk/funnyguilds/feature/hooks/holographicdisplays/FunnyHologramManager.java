package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import net.dzikoysk.funnyguilds.guild.Guild;
import panda.std.Option;

public interface FunnyHologramManager {

    Option<FunnyHologram> createHologram(Guild guild);

    Option<FunnyHologram> deleteHologram(Guild guild);

    Option<Guild> deleteHologram(FunnyHologram hologram);

    Option<FunnyHologram> getFunnyHologram(Guild guild);

}
