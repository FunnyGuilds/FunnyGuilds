package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;

import java.util.List;

class FunnyHologramImpl implements FunnyHologram {

    private final Hologram hologram;

    FunnyHologramImpl(Hologram hologram) {
        this.hologram = hologram;
    }

    @Override
    public void setLocation(Location location) {
        this.hologram.teleport(location.clone());
    }

    @Override
    public void setLines(List<String> lines) {
        this.hologram.clearLines();
        lines.forEach(this.hologram::appendTextLine);
    }

    void delete() {
        this.hologram.delete();
    }

}