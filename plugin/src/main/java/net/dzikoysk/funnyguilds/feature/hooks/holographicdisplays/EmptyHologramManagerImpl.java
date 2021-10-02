package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import net.dzikoysk.funnyguilds.guild.Guild;
import panda.std.Option;

public class EmptyHologramManagerImpl implements FunnyHologramManager {

    public Option<FunnyHologram> getOrCreateHologram(Guild guild) {
        return Option.none();
    }

    public Option<FunnyHologram> deleteHologram(Guild guild) {
        return Option.none();
    }

    public Option<Guild> deleteHologram(FunnyHologram hologram) {
        return Option.none();
    }

    public Option<FunnyHologram> getFunnyHologram(Guild guild) {
        return Option.none();
    }

}
