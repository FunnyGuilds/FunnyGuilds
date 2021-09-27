package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class LocationUtils {

    private LocationUtils() {}

    public static double flatDistance(Location a, Location b) {
        return Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getZ() - a.getZ(), 2));
    }

    public static boolean checkWorld(Player player) {
        List<String> blockedWorlds = FunnyGuilds.getInstance().getPluginConfiguration().blockedWorlds;
        return blockedWorlds != null && blockedWorlds.size() > 0 && blockedWorlds.contains(player.getWorld().getName());
    }

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

        return new Location(world, Integer.parseInt(shs[1]), Integer.parseInt(shs[2]), Integer.parseInt(shs[3]));
    }

    public static boolean equals(Location location, Location to) {
        return (location.getBlockX() == to.getBlockX() && location.getBlockY() == to.getBlockY() && location.getBlockZ() == to.getBlockZ());
    }

    public static boolean equalsFlat(Location location, Location to) {
        return (location.getBlockX() == to.getBlockX() && location.getBlockZ() == to.getBlockZ());
    }

    public static String toString(Location location) {
        if (location == null) {
            return null;
        }

        return location.getWorld().getName() +
                "," +
                location.getBlockX() +
                "," +
                location.getBlockY() +
                "," +
                location.getBlockZ();
    }

}
