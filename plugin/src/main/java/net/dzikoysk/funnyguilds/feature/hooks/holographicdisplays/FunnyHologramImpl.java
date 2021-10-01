package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

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
    public void clearHologram() {
        this.hologram.clearLines();
    }

    @Override
    public void setLines(List<String> lines) {
        this.hologram.clearLines();
        lines.forEach(this.hologram::appendTextLine);
    }

    @Override
    public void addLines(List<String> lines) {
        lines.forEach(this.hologram::appendTextLine);
    }

    @Override
    public void setIconItem(ItemStack item) {
        hologram.clearLines();
        hologram.appendItemLine(item);
    }

    @Override
    public void addIconItem(ItemStack item) {
        hologram.appendItemLine(item);
    }

    void delete() {
        this.hologram.delete();
    }

}