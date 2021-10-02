package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface FunnyHologram {

    FunnyHologram setLocation(Location location);

    void clearHologram();

    FunnyHologram setLines(List<String> lines);

    FunnyHologram addLines(List<String> lines);

    FunnyHologram setIconItem(ItemStack item);

    FunnyHologram addIconItem(ItemStack item);

}
