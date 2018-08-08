package net.dzikoysk.funnyguilds.util.commons.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationUtils {

    public static Location parseLocation(String string) {
        if (string == null) {
            return null;
        }

        String[] shs = string.split(",");

        if (shs.length < 4) {
            return null;
        }

        World world = Bukkit.getWorld(shs[0]);

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        return new Location(world, Integer.valueOf(shs[1]), Integer.valueOf(shs[2]), Integer.valueOf(shs[3]));
    }

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
