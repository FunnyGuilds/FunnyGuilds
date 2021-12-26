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

    /**
     * Gets the copied set of regions.
     *
     * @return set of regions
     * @deprecated for removal in the future, in favour of {@link RegionManager#getRegions()}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Set<Region> getRegions() {
        return FunnyGuilds.getInstance().getRegionManager().getRegions();
    }

    /**
     * Gets the region.
     *
     * @param name the name of region (probably name of guild)
     * @return the region
     * @deprecated for removal in the future, in favour of {@link RegionManager#findByName(String)}
     */
    @Deprecated
    @Nullable
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Region get(String name) {
        return FunnyGuilds.getInstance().getRegionManager().findByName(name).getOrNull();
    }

    /**
     * Gets the region.
     *
     * @param location the location of region
     * @return the region
     * @deprecated for removal in the future, in favour of {@link RegionManager#findRegionAtLocation(Location)}
     */
    @Deprecated
    @Nullable
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Region getAt(Location location) {
        return FunnyGuilds.getInstance().getRegionManager().findRegionAtLocation(location).getOrNull();
    }

    /**
     * Gets the region.
     *
     * @param location the location of region
     * @return the region
     * @deprecated for removal in the future, in favour of {@link RegionManager#findRegionAtLocation(Location)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Option<Region> getAtOpt(Location location) {
        return FunnyGuilds.getInstance().getRegionManager().findRegionAtLocation(location);
    }

    /**
     * Checks if there is a region in the given location.
     *
     * @param location the location of region
     * @return if given location is in region
     * @deprecated for removal in the future, in favour of {@link RegionManager#isInRegion(Location)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static boolean isIn(Location location) {
        return FunnyGuilds.getInstance().getRegionManager().isInRegion(location);
    }

    /**
     * Checks if there is any region in area (used to check if it's possible to create a new guild in given location).
     *
     * @param center the center of region
     * @return if is region nearby
     * @deprecated for removal in the future, in favour of {@link RegionManager#isNearRegion(Location)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static boolean isNear(Location center) {
        return FunnyGuilds.getInstance().getRegionManager().isNearRegion(center);
    }

    /**
     * Add region to storage. If you think you should use this method you probably shouldn't.
     *
     * @param region region to add
     * @deprecated for removal in the future, in favour of {@link RegionManager#addRegion(Region)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static void addRegion(Region region) {
        FunnyGuilds.getInstance().getRegionManager().addRegion(region);
    }

    /**
     * Remove region from storage. If you think you should use this method you probably shouldn't - instead use {@link RegionManager#deleteRegion(Region)}.
     *
     * @param region region to remove
     * @deprecated for removal in the future, in favour of {@link RegionManager#removeRegion(Region)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static void removeRegion(Region region) {
        FunnyGuilds.getInstance().getRegionManager().removeRegion(region);
    }

    /**
     * Delete region in every possible way.
     *
     * @param region region to delete
     * @deprecated for removal in the future, in favour of {@link RegionManager#deleteRegion(Region)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static void delete(Region region) {
        FunnyGuilds.getInstance().getRegionManager().deleteRegion(region);
    }

    public static String toString(@Nullable Region region) {
        return region != null ? region.getName() : "null";
    }

}
