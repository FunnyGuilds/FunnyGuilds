package net.dzikoysk.funnyguilds.feature.hooks.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.EnumFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.nms.Reflections;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class WorldGuard6Hook extends WorldGuardHook {

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

    private WorldGuardPlugin worldGuard;
    private StateFlag noPointsFlag;
    private StateFlag noGuildsFlag;
    private EnumFlag<FriendlyFireStatus> friendlyFireFlag;

    public WorldGuard6Hook(String name) {
        super(name);
    }

    @Override
    public HookInitResult earlyInit() {
        this.worldGuard = WorldGuardPlugin.inst();
        this.noPointsFlag = new StateFlag("fg-no-points", false);
        this.noGuildsFlag = new StateFlag("fg-no-guilds", false);
        this.friendlyFireFlag = new EnumFlag<FriendlyFireStatus>("fg-friendly-fire", FriendlyFireStatus.class) {
            @Override
            public @NotNull FriendlyFireStatus getDefault() {
                return FriendlyFireStatus.INHERIT;
            }
        };

        Class<?> worldGuardPluginClass = Reflections.getClass("com.sk89q.worldguard.bukkit.WorldGuardPlugin");

        Method getInstance = Reflections.getMethod(worldGuardPluginClass, "inst");
        Method getFlagRegistry = Reflections.getMethod(worldGuardPluginClass, "getFlagRegistry");

        try {
            ((FlagRegistry) getFlagRegistry.invoke(getInstance.invoke(null))).register(this.noPointsFlag);
            ((FlagRegistry) getFlagRegistry.invoke(getInstance.invoke(null))).register(this.noGuildsFlag);
            ((FlagRegistry) getFlagRegistry.invoke(getInstance.invoke(null))).register(this.friendlyFireFlag);
        }
        catch (FlagConflictException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            FunnyGuilds.getPluginLogger().error("An error occurred while registering an \"fg-no-points\" worldguard flag", ex);
            return HookInitResult.FAILURE;
        }

        return HookInitResult.SUCCESS;
    }

    @Override
    public boolean isInNonPointsRegion(Location location) {
        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return false;
        }

        return PandaStream.of(regionSet.get().getRegions())
                .find(region -> region.getFlag(this.noPointsFlag) == StateFlag.State.ALLOW)
                .isPresent();
    }

    @Override
    public boolean isInNonGuildsRegion(Location location) {
        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return false;
        }

        return PandaStream.of(regionSet.get().getRegions())
                .find(region -> region.getFlag(this.noGuildsFlag) == StateFlag.State.ALLOW)
                .isPresent();
    }

    @Override
    public FriendlyFireStatus getFriendlyFireStatus(Location location) {
        return this.getRegionSet(location).toStream()
                .flatMap(ApplicableRegionSet::getRegions)
                .map(region -> region.getFlag(this.friendlyFireFlag))
                .filter(friendlyFireStatus -> friendlyFireStatus != FriendlyFireStatus.INHERIT)
                .head()
                .orElseGet(FriendlyFireStatus.INHERIT);
    }

    @Override
    public boolean isInIgnoredRegion(Location location) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return false;
        }

        return PandaStream.of(regionSet.get().getRegions())
                .find(region -> config.assistsRegionsIgnored.contains(region.getId()))
                .isPresent();
    }

    @Override
    public boolean isInRegion(Location location) {
        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return false;
        }

        return regionSet.get().size() != 0;
    }

    @Override
    public Option<ApplicableRegionSet> getRegionSet(Location location) {
        if (location == null || this.worldGuard == null) {
            return Option.none();
        }

        try {
            RegionManager regionManager = (RegionManager) GET_REGION_MANAGER.invokeExact(this.worldGuard, location.getWorld());
            return Option.of((ApplicableRegionSet) GET_APPLICABLE_REGIONS.invokeExact(regionManager, location));
        }
        catch (Throwable throwable) {
            return Option.none();
        }
    }

    @Override
    public List<String> getRegionNames(Location location) {
        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return Collections.emptyList();
        }

        return PandaStream.of(regionSet.get().getRegions())
                .map(ProtectedRegion::getId)
                .toList();
    }

}
