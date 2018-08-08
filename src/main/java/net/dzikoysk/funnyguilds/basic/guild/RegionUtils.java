package net.dzikoysk.funnyguilds.basic.guild;

import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.*;

public final class RegionUtils {

    public static final Set<Region> REGION_LIST = new HashSet<>();

    public static Set<Region> getRegions() {
        return new HashSet<>(REGION_LIST);
    }

    public static Region get(String name) {
        if (name == null) {
            return null;
        }
        
        for (Region region : REGION_LIST) {
            if (region != null && region.getName() != null && region.getName().equalsIgnoreCase(name)) {
                return region;
            }
        }
        
        return null;
    }

    public static boolean isIn(Location loc) {
        for (Region region : REGION_LIST) {
            if (region.isIn(loc)) {
                return true;
            }
        }
        
        return false;
    }

    public static Region getAt(Location loc) {
        for (Region region : REGION_LIST) {
            if (region.isIn(loc)) {
                return region;
            }
        }
        
        return null;
    }

    public static boolean isNear(Location center) {
        if (center == null) {
            return false;
        }
        
        PluginConfig s = Settings.getConfig();
        int i = s.regionSize;
        if (s.enlargeItems != null) {
            i += (s.enlargeItems.size() * s.enlargeSize);
        }
        
        int requiredDistance = (2 * i) + s.regionMinDistance;
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
            
            if (center.distance(region.getCenter()) < requiredDistance) {
                return true;
            }
        }
        
        return false;
    }

    public static void delete(@Nullable Region region) {
        if (region == null) {
            return;
        }

        if (Settings.getConfig().dataType.flat) {
            Flat.getRegionFile(region).delete();
        }
        
        if (Settings.getConfig().dataType.mysql) {
            new DatabaseRegion(region).delete();
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
        return region != null ? region.getName() : null;
    }

}
