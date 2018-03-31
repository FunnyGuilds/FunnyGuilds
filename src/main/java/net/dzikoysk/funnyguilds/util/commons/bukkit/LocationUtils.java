package net.dzikoysk.funnyguilds.util.commons.bukkit;

import org.bukkit.Location;

public final class LocationUtils {

    public static boolean equals(Location f, Location s) {
        return (f.getBlockX() == s.getBlockX() && f.getBlockY() == s.getBlockY() && f.getBlockZ() == s.getBlockZ());
    }

    public static boolean equalsFlat(Location f, Location s) {
        return (f.getBlockX() == s.getBlockX() && f.getBlockZ() == s.getBlockZ());
    }

    public static String toString(Location loc) {
        if (loc == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(loc.getWorld().getName());
        sb.append(",");
        sb.append(loc.getBlockX());
        sb.append(",");
        sb.append(loc.getBlockY());
        sb.append(",");
        sb.append(loc.getBlockZ());

        return sb.toString();
    }
    
    private LocationUtils() {}

}
