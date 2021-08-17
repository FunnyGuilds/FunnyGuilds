package net.dzikoysk.funnyguilds.feature.hooks.worldguard;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.Location;

import java.util.List;

public interface WorldGuardHook {

    boolean isInNonPointsRegion(Location location);
    boolean isInIgnoredRegion(Location location);
    boolean isInRegion(Location location);
    ApplicableRegionSet getRegionSet(Location location);
    List<String> getRegionNames(Location location);

    void init();
}
