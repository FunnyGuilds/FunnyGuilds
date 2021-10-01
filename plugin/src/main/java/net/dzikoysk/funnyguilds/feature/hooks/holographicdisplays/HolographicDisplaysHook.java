package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.sections.HologramConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.Bukkit;
import panda.std.Option;
import panda.std.stream.PandaStream;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class HolographicDisplaysHook implements FunnyHologramManager {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final Map<Guild, FunnyHologramImpl> holograms = Collections.synchronizedMap(new HashMap<>());

    private HolographicDisplaysHook(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfiguration();
    }

    @Override
    public Option<FunnyHologram> createHologram(Guild guild) {
        this.deleteHologram(guild);

        HologramConfiguration hologramConfig = config.heartConfig.hologram;
        return guild.getCenter()
                .map(location -> location.add(hologramConfig.locationCorrection.toLocation(location.getWorld())))
                .map(location -> HologramsAPI.createHologram(plugin, location))
                .map(FunnyHologramImpl::new)
                .peek(hologram -> holograms.put(guild, hologram))
                .is(FunnyHologram.class);
    }

    @Override
    public void deleteHologram(Guild guild) {
        FunnyHologramImpl hologramHook = holograms.remove(guild);

        if (hologramHook == null) {
            return;
        }

        hologramHook.delete();
    }

    @Override
    public void deleteHologram(FunnyHologram hologram) {
        PandaStream.of(holograms.entrySet())
                .find(entry -> entry.getValue().equals(hologram))
                .map(Map.Entry::getKey)
                .map(holograms::remove)
                .peek(FunnyHologramImpl::delete);
    }

    @Override
    public Option<FunnyHologram> getFunnyHologram(Guild guild) {
        return Option.of(this.holograms.get(guild));
    }

    public static HolographicDisplaysHook createAndRunHandler(FunnyGuilds plugin) {
        HologramConfiguration hologramConfig = plugin.getPluginConfiguration().heartConfig.hologram;
        HolographicDisplaysHook holographicDisplaysHook = new HolographicDisplaysHook(plugin);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new HologramUpdateHandler(plugin), 100L, hologramConfig.updateInterval);

        return holographicDisplaysHook;
    }

}
