package net.dzikoysk.funnyguilds.guild.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.Location;
import panda.std.Option;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RegionUtils {

    public static final Set<Region> REGION_LIST = ConcurrentHashMap.newKeySet();

    public static Set<Region> getRegions() {
        return new HashSet<>(REGION_LIST);
    }

    public static Region get(String name) {
        if (name == null) {
            return null;
        }

        for (Region region : REGION_LIST) {
            if (name.equalsIgnoreCase(region.getName())) {
                return region;
            }
        }

        return null;
    }

    public static boolean isIn(Location location) {
        for (Region region : REGION_LIST) {
            if (region.isIn(location)) {
                return true;
            }
        }

        return false;
    }

    public static Region getAt(Location location) {
        for (Region region : REGION_LIST) {
            if (region.isIn(location)) {
                return region;
            }
        }

        return null;
    }

    public static Option<Region> getAtOpt(Location location) {
        return Option.of(getAt(location));
    }

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

        for (Region region : REGION_LIST) {
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

    public static void delete(@Nullable Region region) {
        if (region == null) {
            return;
        }

        if (FunnyGuilds.getInstance().getDataModel() instanceof FlatDataModel) {
            FlatDataModel dataModel = (FlatDataModel) FunnyGuilds.getInstance().getDataModel();
            dataModel.getRegionFile(region).delete();
        }

        if (FunnyGuilds.getInstance().getDataModel() instanceof SQLDataModel) {
            DatabaseRegion.delete(region);
        }

        region.delete();
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

    public static void addRegion(Region region) {
        REGION_LIST.add(region);
    }

    public static void removeRegion(Region region) {
        REGION_LIST.remove(region);
    }

    public static String toString(@Nullable Region region) {
        return region != null ? region.getName() : "null";
    }

}
