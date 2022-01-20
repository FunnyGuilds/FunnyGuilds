package net.dzikoysk.funnyguilds.feature.hooks.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import java.util.List;
import net.dzikoysk.funnyguilds.feature.hologram.FunnyHologram;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class FunnyHologramImpl implements FunnyHologram {

    private final Hologram hologram;

    public FunnyHologramImpl(Hologram hologram) {
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

    public void delete() {
        this.hologram.delete();
    }

}