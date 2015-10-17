package net.dzikoysk.funnyguilds.util;

import org.bukkit.Location;

public class Unparser {

    public static String unparse(Location loc) {
        if (loc == null)
            return null;
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

}
