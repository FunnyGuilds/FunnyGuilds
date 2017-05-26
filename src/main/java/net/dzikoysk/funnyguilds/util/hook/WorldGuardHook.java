package net.dzikoysk.funnyguilds.util.hook;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

public class WorldGuardHook {
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
        for (ProtectedRegion region : worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location)) {
            if (region.getFlag(noPointsFlag) == StateFlag.State.ALLOW) {
                return true;
            }
        }

        return false;
    }
}
