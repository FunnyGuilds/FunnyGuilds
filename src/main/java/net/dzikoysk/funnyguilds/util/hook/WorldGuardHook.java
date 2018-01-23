package net.dzikoysk.funnyguilds.util.hook;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

public final class WorldGuardHook {
    private static WorldGuardPlugin worldGuard;
    private static StateFlag noPointsFlag;

    public static void initWorldGuard() {
        WorldGuardHook.worldGuard = WorldGuardPlugin.inst();
        noPointsFlag = new StateFlag("fg-no-points", false);
        worldGuard.getFlagRegistry().register(noPointsFlag);
    }

    public static boolean isOnNonPointsRegion(Location location) {
        if (location == null) {
            return false;
        }
        
        if (worldGuard == null) {
            return false;
        }
        
        RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
        if (regionManager == null) {
            return false;
        }
        
        for (ProtectedRegion region : regionManager.getApplicableRegions(location)) {
            if (region.getFlag(noPointsFlag) == StateFlag.State.ALLOW) {
                return true;
            }
        }

        return false;
    }

    public static boolean isOnRegion(Location location) {
        if (location == null) {
            return false;
        }
        
        if (worldGuard == null) {
            return false;
        }
        
        RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
        if (regionManager == null) {
            return false;
        }

        return regionManager.getApplicableRegions(location).size() != 0;
    }
}
