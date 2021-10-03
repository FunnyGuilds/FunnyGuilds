package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationConfiguration extends OkaeriConfig {

    public double x;
    public double y;
    public double z;

    public LocationConfiguration(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

}
