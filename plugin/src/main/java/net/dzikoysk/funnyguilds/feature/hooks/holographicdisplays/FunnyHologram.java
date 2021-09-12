package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import org.bukkit.Location;

import java.util.List;

public interface FunnyHologram {

    void setLocation(Location location);

    void setLines(List<String> lines);

}
