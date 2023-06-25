package net.dzikoysk.funnyguilds.feature.hooks.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Location;
import org.bukkit.World;
import panda.std.Option;

public class WorldGuard6Hook extends WorldGuardHook {

    private static final MethodHandle GET_REGION_MANAGER;
    private static final MethodHandle GET_APPLICABLE_REGIONS;
    private static final MethodHandle GET_FLAG_REGISTRY;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            GET_REGION_MANAGER = lookup.findVirtual(WorldGuardPlugin.class, "getRegionManager",
                    MethodType.methodType(RegionManager.class, World.class));
            GET_APPLICABLE_REGIONS = lookup.findVirtual(RegionManager.class, "getApplicableRegions",
                    MethodType.methodType(ApplicableRegionSet.class, Location.class));
            GET_FLAG_REGISTRY = lookup.findVirtual(WorldGuardPlugin.class, "getFlagRegistry",
                    MethodType.methodType(FlagRegistry.class));
        } catch (NoSuchMethodException | IllegalAccessException ex) {
            throw new RuntimeException("Could not properly initialize WorldGuard 6.0+ hook!", ex);
        }
    }

    private WorldGuardPlugin worldGuard;

    public WorldGuard6Hook(String name) {
        super(name);
    }

    @Override
    public HookInitResult earlyInit() {
        this.worldGuard = WorldGuardPlugin.inst();
        this.initFlags();
        return HookInitResult.SUCCESS;
    }

    @Override
    protected void registerFlags(Flag<?>... flags) {
        try {
            FlagRegistry flagRegistry = (FlagRegistry) GET_FLAG_REGISTRY.invoke(this.worldGuard);
            for (Flag<?> flag : flags) {
                flagRegistry.register(flag);
            }
        } catch (Throwable ex) {
            FunnyGuilds.getPluginLogger().error("An error occurred while registering an worldguard flags", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Option<ApplicableRegionSet> getRegionSet(Location location) {
        if (location == null || this.worldGuard == null) {
            return Option.none();
        }

        try {
            RegionManager regionManager = (RegionManager) GET_REGION_MANAGER.invokeExact(this.worldGuard, location.getWorld());
            return Option.of((ApplicableRegionSet) GET_APPLICABLE_REGIONS.invokeExact(regionManager, location));
        } catch (Throwable throwable) {
            return Option.none();
        }
    }

}
