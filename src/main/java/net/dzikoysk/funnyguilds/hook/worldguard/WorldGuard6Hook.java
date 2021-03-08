package net.dzikoysk.funnyguilds.hook.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class WorldGuard6Hook implements WorldGuardHook {

    private static final MethodHandle GET_REGION_MANAGER;
    private static final MethodHandle GET_APPLICABLE_REGIONS;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            GET_REGION_MANAGER = lookup.findVirtual(WorldGuardPlugin.class, "getRegionManager",
                    MethodType.methodType(RegionManager.class, World.class));
            GET_APPLICABLE_REGIONS = lookup.findVirtual(RegionManager.class, "getApplicableRegions",
                    MethodType.methodType(ApplicableRegionSet.class, Location.class));
        }
        catch (NoSuchMethodException | IllegalAccessException ex) {
            throw new RuntimeException("Could not properly initialize WorldGuard 6.0+ hook!", ex);
        }
    }

    private Method getInstance;
    private Method getFlagRegistry;

    private WorldGuardPlugin worldGuard;
    private StateFlag noPointsFlag;

    @Override
    public void init() {
        worldGuard = WorldGuardPlugin.inst();
        noPointsFlag = new StateFlag("fg-no-points", false);

        Class<?> worldGuardPluginClass = Reflections.getClass("com.sk89q.worldguard.bukkit.WorldGuardPlugin");

        getInstance = Reflections.getMethod(worldGuardPluginClass, "inst");
        getFlagRegistry = Reflections.getMethod(worldGuardPluginClass, "getFlagRegistry");

        try {
            ((FlagRegistry) getFlagRegistry.invoke(getInstance.invoke(null))).register(noPointsFlag);
        }
        catch (FlagConflictException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            FunnyGuilds.getPluginLogger().error("An error occurred while registering an \"fg-no-points\" worldguard flag", ex);
        }
    }

    @Override
    public boolean isInNonPointsRegion(Location location) {
        ApplicableRegionSet regionSet = getRegionSet(location);

        if (regionSet == null) {
            return false;
        }

        for (ProtectedRegion region : regionSet) {
            if (region.getFlag(noPointsFlag) == StateFlag.State.ALLOW) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isInIgnoredRegion(Location location) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        ApplicableRegionSet regionSet = getRegionSet(location);

        if (regionSet == null) {
            return false;
        }

        return regionSet.getRegions()
                .stream()
                .anyMatch(region -> config.assistsRegionsIgnored.contains(region.getId()));
    }

    @Override
    public boolean isInRegion(Location location) {
        ApplicableRegionSet regionSet = getRegionSet(location);

        if (regionSet == null) {
            return false;
        }

        return regionSet.size() != 0;
    }

    @Override
    public ApplicableRegionSet getRegionSet(Location location) {
        if (location == null || worldGuard == null) {
            return null;
        }

        try {
            RegionManager regionManager = (RegionManager) GET_REGION_MANAGER.invokeExact(worldGuard, location.getWorld());
            return (ApplicableRegionSet) GET_APPLICABLE_REGIONS.invokeExact(regionManager, location);
        }
        catch (Throwable ex) {
            return null;
        }
    }

    @Override
    public List<String> getRegionNames(Location location) {
        ApplicableRegionSet regionSet = getRegionSet(location);

        return regionSet == null ? null : regionSet.getRegions().stream()
                .map(ProtectedRegion::getId)
                .collect(Collectors.toList());
    }
}
