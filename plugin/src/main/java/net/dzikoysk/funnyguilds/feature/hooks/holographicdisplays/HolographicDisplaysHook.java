package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.subcomponents.HologramConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import panda.std.Option;
import panda.std.stream.PandaStream;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class HolographicDisplaysHook implements FunnyHologramManager {

    private final FunnyGuilds plugin;
    private final Map<Guild, FunnyHologramImpl> holograms = Collections.synchronizedMap(new HashMap<>());

    private HolographicDisplaysHook(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public Option<FunnyHologram> createHologram(Guild guild) {
        this.deleteHologram(guild);

        //TODO: Zrobić to ładniej
        HologramConfiguration hologramConfig = plugin.getPluginConfiguration().heartConfig.hologram;
        Location center = guild.getRegion().getCenter();
        Location hologramLoc = center.clone().add(hologramConfig.locationCorrection.toLocation(center.getWorld()));

        return guild.getEnderCrystal()
                .map(location -> HologramsAPI.createHologram(plugin, hologramLoc))
                .map(FunnyHologramImpl::new)
                .peek(hologram -> holograms.put(guild, hologram))
                .peek(funnyHologram -> funnyHologram.setLocation(hologramLoc))
                .is(FunnyHologram.class);
    }

    @Override
    public void deleteHologram(Guild guild) {
        FunnyHologramImpl hologramHook = holograms.remove(guild);

        if (hologramHook == null) {
            return;
        }

        hologramHook.delete();
        throw new IllegalArgumentException();
    }

    @Override
    public void deleteHologram(FunnyHologram hologram) {
        PandaStream.of(holograms.entrySet())
                .find(entry -> entry.getValue().equals(hologram))
                .map(Map.Entry::getKey)
                .map(holograms::remove)
                .peek(FunnyHologramImpl::delete);
        throw new IllegalArgumentException();
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
