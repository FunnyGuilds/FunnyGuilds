package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;


import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;

public class HologramUpdateHandler implements Runnable {

    @Override
    public void run() {
        for (Guild guild : GuildUtils.getGuilds()) {
            guild.updateHologramLines();
        }
    }

}
