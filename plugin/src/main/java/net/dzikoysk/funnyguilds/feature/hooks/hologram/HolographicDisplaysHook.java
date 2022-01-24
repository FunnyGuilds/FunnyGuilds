package net.dzikoysk.funnyguilds.feature.hooks.hologram;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.sections.HologramConfiguration;
import net.dzikoysk.funnyguilds.feature.hologram.FunnyHologram;
import net.dzikoysk.funnyguilds.feature.hologram.HologramUpdateHandler;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import panda.std.Option;
import panda.std.stream.PandaStream;

public final class HolographicDisplaysHook extends HologramHook {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final Map<Guild, FunnyHologramImpl> holograms = new ConcurrentHashMap<>();

    public HolographicDisplaysHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
        this.config = plugin.getPluginConfiguration();
    }

    @Override
    public HookInitResult init() {
        HologramConfiguration hologramConfig = config.heart.hologram;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new HologramUpdateHandler(plugin), 100L, hologramConfig.updateInterval);

        return HookInitResult.SUCCESS;
    }

    @Override
    public Option<FunnyHologram> getOrCreateHologram(Guild guild) {
        return this.getFunnyHologram(guild).orElse(() -> this.getCorrectedLocation(guild)
                .map(location -> HologramsAPI.createHologram(plugin, location))
                .map(FunnyHologramImpl::new)
                .peek(hologram -> holograms.put(guild, hologram))
                .is(FunnyHologram.class));
    }

    @Override
    public Option<FunnyHologram> deleteHologram(Guild guild) {
        return Option.of(holograms.remove(guild))
                .peek(FunnyHologramImpl::delete)
                .is(FunnyHologram.class);
    }

    @Override
    public Option<Guild> deleteHologram(FunnyHologram hologram) {
        return PandaStream.of(holograms.entrySet())
                .find(entry -> entry.getValue().equals(hologram))
                .map(Map.Entry::getKey)
                .peek(guild -> Option.of(holograms.remove(guild))
                        .peek(FunnyHologramImpl::delete));
    }

    @Override
    public Option<FunnyHologram> getFunnyHologram(Guild guild) {
        return Option.of(this.holograms.get(guild));
    }

    @Override
    public Option<Location> getCorrectedLocation(Guild guild) {
        HologramConfiguration hologram = config.heart.hologram;

        return guild.getCenter()
                .map(location -> location.add(hologram.locationCorrection.toLocation(location.getWorld())));
    }

}
