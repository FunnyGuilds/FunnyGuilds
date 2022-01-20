package net.dzikoysk.funnyguilds.feature.hologram;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface FunnyHologram {

    FunnyHologram setLocation(Location location);

    void clearHologram();

    FunnyHologram setLines(List<String> lines);

    FunnyHologram addLines(List<String> lines);

    FunnyHologram setIconItem(ItemStack item);

    FunnyHologram addIconItem(ItemStack item);

}
