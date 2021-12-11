package net.dzikoysk.funnyguilds.guild;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import panda.std.Option;

public final class RegionUtils {

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Set<Region> getRegions() {
        return FunnyGuilds.getInstance().getRegionManager().getRegions();
    }

    @Deprecated
    @Nullable
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Region get(String name) {
        return FunnyGuilds.getInstance().getRegionManager().findByName(name).getOrNull();
    }

    @Deprecated
    @Nullable
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Region getAt(Location location) {
        return FunnyGuilds.getInstance().getRegionManager().findRegionAtLocation(location).getOrNull();
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Option<Region> getAtOpt(Location location) {
        return FunnyGuilds.getInstance().getRegionManager().findRegionAtLocation(location);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static boolean isIn(Location location) {
        return FunnyGuilds.getInstance().getRegionManager().isInRegion(location);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static boolean isNear(Location center) {
        return FunnyGuilds.getInstance().getRegionManager().isNearRegion(center);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static void addRegion(Region region) {
        FunnyGuilds.getInstance().getRegionManager().addRegion(region);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static void removeRegion(Region region) {
        FunnyGuilds.getInstance().getRegionManager().removeRegion(region);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static void delete(Region region) {
        FunnyGuilds.getInstance().getRegionManager().deleteRegion(region);
    }

    public static Set<String> getNames(Collection<Region> regions) {
        return regions.stream()
                .filter(Objects::nonNull)
                .map(Region::getName)
                .collect(Collectors.toSet());
    }

    public static String toString(@Nullable Region region) {
        return region != null ? region.getName() : "null";
    }

}
