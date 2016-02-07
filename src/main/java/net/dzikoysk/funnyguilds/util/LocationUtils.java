package net.dzikoysk.funnyguilds.util;

import org.bukkit.Location;

public class LocationUtils {

    public static boolean equals(Location f, Location s) {
        return f.getBlockX() == s.getBlockX() && f.getBlockY() == s.getBlockY() && f.getBlockZ() == s.getBlockZ();
    }

    public static boolean equalsFlat(Location f, Location s) {
        return f.getBlockX() == s.getBlockX() && f.getBlockZ() == s.getBlockZ();
    }

}
