package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;


import net.dzikoysk.funnyguilds.guild.GuildUtils;
import panda.std.stream.PandaStream;

public class HologramUpdateHandler implements Runnable {

    @Override
    public void run() {
        // TODO Config

        PandaStream.of(GuildUtils.getGuilds())
                .forEach(guild -> guild.updateHologram(hologram -> {
                    // TODO PlaceHolders
                    hologram.setLines(null);
                }));
    }

}
