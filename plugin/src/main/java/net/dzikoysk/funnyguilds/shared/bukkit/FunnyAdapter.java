package net.dzikoysk.funnyguilds.shared.bukkit;

import net.dzikoysk.funnyguilds.shared.Position;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class FunnyAdapter {

    public static Position adapt(Location location) {
        World world = location.getWorld();
        String wordName = world == null ? null : world.getName();

        return new Position(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), wordName);
    }

    public static Location adapt(Position position) {
        World world = position.getWorld()
                .map(Bukkit::getWorld)
                .orNull();

        return new Location(world, position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch());
    }

}
