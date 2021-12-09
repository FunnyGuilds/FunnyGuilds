package net.dzikoysk.funnyguilds.guild;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Location;
import panda.std.Option;

public final class RegionUtils {

    @Deprecated
    public static Set<Region> getRegions() {
        return FunnyGuilds.getInstance().getRegionManager().getRegions();
    }

    @Deprecated
    @Nullable
    public static Region get(String name) {
        return FunnyGuilds.getInstance().getRegionManager().findByName(name).getOrNull();
    }

    @Deprecated
    @Nullable
    public static Region getAt(Location location) {
        return FunnyGuilds.getInstance().getRegionManager().findRegionAtLocation(location).getOrNull();
    }

    @Deprecated
    public static Option<Region> getAtOpt(Location location) {
        return FunnyGuilds.getInstance().getRegionManager().findRegionAtLocation(location);
    }

    @Deprecated
    public static boolean isIn(Location location) {
        return FunnyGuilds.getInstance().getRegionManager().isInRegion(location);
    }

    @Deprecated
    public static boolean isNear(Location center) {
        return FunnyGuilds.getInstance().getRegionManager().isNearRegion(center);
    }

    @Deprecated
    public static void addRegion(Region region) {
        FunnyGuilds.getInstance().getRegionManager().addRegion(region);
    }

    @Deprecated
    public static void removeRegion(Region region) {
        FunnyGuilds.getInstance().getRegionManager().removeRegion(region);
    }

    @Deprecated
    public static void delete(Region region) {
        FunnyGuilds.getInstance().getRegionManager().deleteRegion(region);
    }

    public static Set<String> getNamesOfRegions(Collection<Region> regions) {
        return regions.stream()
                .filter(Objects::nonNull)
                .map(Region::getName)
                .collect(Collectors.toSet());
    }

    @Deprecated
    public static String toString(@Nullable Region region) {
        return region != null ? region.getName() : "null";
    }

}
