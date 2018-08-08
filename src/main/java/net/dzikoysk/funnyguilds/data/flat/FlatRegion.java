package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.Yamler;
import org.bukkit.Location;

import java.io.File;

public class FlatRegion {

    private final Region region;

    public FlatRegion(Region region) {
        this.region = region;
    }

    public static Region deserialize(File file) {
        Yamler pc = new Yamler(file);
        String name = pc.getString("name");
        String cs = pc.getString("center");
        int size = pc.getInt("size");
        int enlarge = pc.getInt("enlarge");
        pc = null;

        if (name == null || cs == null) {
            FunnyLogger.error("Cannot deserialize region! Caused by: name/center is null");
            return null;
        }

        Location center = Parser.parseLocation(cs);
        if (center == null) {
            FunnyLogger.error("Cannot deserialize region! Caused by: center is null");
            return null;
        }

        if (size < 1) {
            size = Settings.getConfig().regionSize;
        }

        Object[] values = new Object[4];
        values[0] = name;
        values[1] = center;
        values[2] = size;
        values[3] = enlarge;
        return DeserializationUtils.deserializeRegion(values);
    }

    public boolean serialize() {
        File file = Flat.loadCustomFile(BasicType.REGION, region.getName());
        Yamler pc = new Yamler(file);
        pc.set("name", region.getName());
        pc.set("center", LocationUtils.toString(region.getCenter()));
        pc.set("size", region.getSize());
        pc.set("enlarge", region.getEnlarge());
        pc.save();
        pc = null;
        return true;
    }

}
