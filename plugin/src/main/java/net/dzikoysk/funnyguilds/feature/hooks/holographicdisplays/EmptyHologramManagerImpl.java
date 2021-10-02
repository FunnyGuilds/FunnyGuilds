package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.Location;
import panda.std.Option;

public class EmptyHologramManagerImpl implements FunnyHologramManager {

    @Override
    public Option<FunnyHologram> getOrCreateHologram(Guild guild) {
        return Option.none();
    }

    @Override
    public Option<FunnyHologram> deleteHologram(Guild guild) {
        return Option.none();
    }

    @Override
    public Option<Guild> deleteHologram(FunnyHologram hologram) {
        return Option.none();
    }

    @Override
    public Option<FunnyHologram> getFunnyHologram(Guild guild) {
        return Option.none();
    }

    @Override
    public Option<Location> getCorrectedLocation(Guild guild) {
        return Option.none();
    }

}
