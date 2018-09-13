package net.dzikoysk.funnyguilds.hook;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public final class WorldGuardHook {
    
    private static final Method GET_INSTANCE;
    private static final Method GET_FLAG_REGISTRY;
    
    private static WorldGuardPlugin worldGuard;
    private static StateFlag noPointsFlag;

    static {
        if (Reflections.USE_PRE_13_METHODS) {
            final Class<?> worldGuardPluginClass = Reflections.getClass("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
            
            GET_INSTANCE = Reflections.getMethod(worldGuardPluginClass, "inst");
            GET_FLAG_REGISTRY = Reflections.getMethod(worldGuardPluginClass, "getFlagRegistry");
        } else {
            final Class<?> worldGuardClass = Reflections.getClass("com.sk89q.worldguard.WorldGuard");
            
            GET_INSTANCE = Reflections.getMethod(worldGuardClass, "getInstance");
            GET_FLAG_REGISTRY = Reflections.getMethod(worldGuardClass, "getFlagRegistry");
        }
    }
    
    public static void initWorldGuard() {
        WorldGuardHook.worldGuard = WorldGuardPlugin.inst();
        noPointsFlag = new StateFlag("fg-no-points", false);
        
        try {
            ((FlagRegistry) GET_FLAG_REGISTRY.invoke(GET_INSTANCE.invoke(null))).register(noPointsFlag);
        } catch (FlagConflictException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            FunnyGuildsLogger.error("An error occurred while registering an \"fg-no-points\" worldguard flag");
            if (FunnyGuildsLogger.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
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

    public static boolean isInIgnoredRegion(Location location) {
        if (!isInRegion(location)) {
            return false;
        }

        PluginConfig config = Settings.getConfig();

        return getRegionSet(location).getRegions()
            .stream()
            .anyMatch(region -> config.assistsRegionsIgnored.contains(region.getId()));
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
        return regionSet == null ? null : regionSet.getRegions().stream()
                .map(ProtectedRegion::getId)
                .collect(Collectors.toList());
    }
    
    private WorldGuardHook() {}
    
}
