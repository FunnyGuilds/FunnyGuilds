package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

class FunnyHologramImpl implements FunnyHologram {

    private final Hologram hologram;

    FunnyHologramImpl(Hologram hologram) {
        this.hologram = hologram;
    }

    @Override
    public FunnyHologram setLocation(Location location) {
        this.hologram.teleport(location.clone());
        return this;
    }

    @Override
    public void clearHologram() {
        this.hologram.clearLines();
    }

    @Override
    public FunnyHologram setLines(List<String> lines) {
        this.hologram.clearLines();
        lines.forEach(this.hologram::appendTextLine);
        return this;
    }

    @Override
    public FunnyHologram addLines(List<String> lines) {
        lines.forEach(this.hologram::appendTextLine);
        return this;
    }

    @Override
    public FunnyHologram setIconItem(ItemStack item) {
        hologram.clearLines();
        hologram.appendItemLine(item);
        return this;
    }

    @Override
    public FunnyHologram addIconItem(ItemStack item) {
        hologram.appendItemLine(item);
        return this;
    }

    void delete() {
        this.hologram.delete();
    }

}