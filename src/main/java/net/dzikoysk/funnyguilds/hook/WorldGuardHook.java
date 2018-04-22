package net.dzikoysk.funnyguilds.hook;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

public final class WorldGuardHook {
    
    private static WorldGuardPlugin worldGuard;
    private static StateFlag noPointsFlag;

    public static void initWorldGuard() {
        WorldGuardHook.worldGuard = WorldGuardPlugin.inst();
        noPointsFlag = new StateFlag("fg-no-points", false);
        worldGuard.getFlagRegistry().register(noPointsFlag);
    }

    public static boolean isInNonPointsRegion(Location location) {
        if (!isInRegion(location)) {
            return false;
        }
        
        for (ProtectedRegion region : getRegionSet(location)) {
            if (region.getFlag(noPointsFlag) == StateFlag.State.ALLOW) {
                return true;
            }
        }

        return false;
    }

    public static boolean isInRegion(Location location) {
        ApplicableRegionSet regionSet = getRegionSet(location);
        if (regionSet == null) {
            return false;
        }

        return regionSet.size() != 0;
    }
    
    public static ApplicableRegionSet getRegionSet(Location location) {        
        if (location == null || worldGuard == null) {
            return null;
        }
        
        RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
        if (regionManager == null) {
            return null;
        }

        return regionManager.getApplicableRegions(location);
    }

    public static List<String> getRegionNames(Location location) {
        ApplicableRegionSet regionSet = getRegionSet(location);
        return regionSet != null ? regionSet.getRegions().stream().map(ProtectedRegion::getId)
                                                                  .collect(Collectors.toList())
                                 : null;
    }
    
}
