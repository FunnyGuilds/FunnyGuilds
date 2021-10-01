package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface FunnyHologram {

    void setLocation(Location location);

    void clearHologram();

    void setLines(List<String> lines);

    void addLines(List<String> lines);

    void setIconItem(ItemStack item);

    void addIconItem(ItemStack item);

}
