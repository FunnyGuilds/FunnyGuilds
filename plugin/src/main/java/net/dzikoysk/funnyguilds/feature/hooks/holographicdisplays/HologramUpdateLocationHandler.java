package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;


import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.sections.HologramConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import org.bukkit.Bukkit;

public class HologramUpdateLocationHandler implements Runnable {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final GuildManager guildManager;

    public HologramUpdateLocationHandler(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfiguration();
        this.guildManager = plugin.getGuildManager();
    }

    @Override
    public void run() {
        HologramConfiguration hologramConfig = config.heart.hologram;

        if (!hologramConfig.enabled) {
            return;
        }

        HookManager.HOLOGRAPHIC_DISPLAYS.peek(hologramManager -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (Guild guild : guildManager.getGuilds()) {
                    hologramManager.getCorrectedLocation(guild)
                            .peek(location -> hologramManager.getFunnyHologram(guild)
                                    .peek(hologram -> hologram.setLocation(location)));
                }
            });
        });
    }

}
