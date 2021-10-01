package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;


import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.subcomponents.HologramConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import org.bukkit.Bukkit;

public class HologramUpdateLocationHandler implements Runnable {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;

    public HologramUpdateLocationHandler(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfiguration();
    }

    @Override
    public void run() {
        HologramConfiguration hologramConfig = config.heartConfig.hologram;

        if (!hologramConfig.enabled) {
            return;
        }

        for (Guild guild : GuildUtils.getGuilds()) {
            Bukkit.getScheduler().runTask(plugin, () -> guild.updateHologram(hologram -> guild.getCenter()
                    .map(location -> location.add(hologramConfig.locationCorrection.toLocation(location.getWorld())))
                    .peek(hologram::setLocation)));
        }
    }

}
