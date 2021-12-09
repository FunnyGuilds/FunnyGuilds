package net.dzikoysk.funnyguilds.guild;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
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
    public static boolean isIn(Location location) {
        return FunnyGuilds.getInstance().getRegionManager().isInRegion(location);
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
    public static boolean isNear(Location center) {
        if (center == null) {
            return false;
        }

        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        int size = config.regionSize;

        if (config.enlargeItems != null) {
            size += (config.enlargeItems.size() * config.enlargeSize);
        }

        int requiredDistance = (2 * size) + config.regionMinDistance;

        for (Region region : getRegions()) {
            if (region.getCenter() == null) {
                continue;
            }

            if (region.getCenter().equals(center)) {
                continue;
            }

            if (!center.getWorld().equals(region.getCenter().getWorld())) {
                continue;
            }

            if (LocationUtils.flatDistance(center, region.getCenter()) < requiredDistance) {
                return true;
            }
        }

        return false;
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

    public static List<String> getNames(Collection<Region> lsg) {
        List<String> list = new ArrayList<>();
        if (lsg == null) {
            return list;
        }

        for (Region r : lsg) {
            if (r != null && r.getName() != null) {
                list.add(r.getName());
            }
        }

        return list;
    }

    @Deprecated
    public static String toString(@Nullable Region region) {
        return region != null ? region.getName() : "null";
    }

}
