package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.Bukkit;
import panda.std.Option;
import panda.std.stream.PandaStream;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class HolographicDisplaysHook implements FunnyHologramManager {

    private final FunnyGuilds plugin;
    private final Map<Guild, FunnyHologramImpl> holograms = Collections.synchronizedMap(new HashMap<>());

    HolographicDisplaysHook(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public Option<FunnyHologram> createHologram(Guild guild) {
        this.deleteHologram(guild);

        return guild.getEnderCrystal()
                .map(location -> HologramsAPI.createHologram(plugin, location))
                .map(FunnyHologramImpl::new)
                .peek(hologram -> holograms.put(guild, hologram))
                .map(funnyHologram -> funnyHologram); // TODO: panda-utilities update
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
        HolographicDisplaysHook holographicDisplaysHook = new HolographicDisplaysHook(plugin);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new HologramUpdateHandler(), 400L, 200L);

        return holographicDisplaysHook;
    }

}
