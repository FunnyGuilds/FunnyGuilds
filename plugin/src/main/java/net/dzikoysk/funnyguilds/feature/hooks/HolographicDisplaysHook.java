package net.dzikoysk.funnyguilds.feature.hooks;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HolographicDisplaysHook {

    private final FunnyGuilds plugin;
    private final Map<Guild, HologramHook> holograms = Collections.synchronizedMap(new HashMap<>());

    public HolographicDisplaysHook(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public HologramHook createHologram(Guild guild) {
        this.deleteHologram(guild);
        Hologram hologram = HologramsAPI.createHologram(plugin, guild.getEnderCrystal());
        HologramHook hologramHook = new HologramHook(hologram);

        holograms.put(guild, hologramHook);
        return hologramHook;
    }

    public void deleteHologram(Guild guild) {
        HologramHook hologramHook = holograms.remove(guild);

        if (hologramHook == null) {
            return;
        }

        hologramHook.hologram.delete();
    }

    public static class HologramHook {

        private final Hologram hologram;

        private HologramHook(Hologram hologram) {
            this.hologram = hologram;
        }

        public void setLocation(Location location) {
            this.hologram.teleport(location);
        }

        public void setLines(List<String> lines) {
            this.hologram.clearLines();
            lines.forEach(this.hologram::appendTextLine);
        }
    }

}
