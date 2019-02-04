package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.YamlWrapper;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Location;

import java.io.File;

public class FlatRegion {

    private final Region region;

    public FlatRegion(Region region) {
        this.region = region;
    }

    public static Region deserialize(File file) {
        YamlWrapper pc = new YamlWrapper(file);
        String name = pc.getString("name");
        String cs = pc.getString("center");
        int size = pc.getInt("size");
        int enlarge = pc.getInt("enlarge");

        if (name == null || cs == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("Cannot deserialize region! Caused by: name/center is null");
            return null;
        }

        Location center = LocationUtils.parseLocation(cs);

        if (center == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("Cannot deserialize region! Caused by: center is null");
            return null;
        }

        if (size < 1) {
            size = FunnyGuilds.getInstance().getPluginConfiguration().regionSize;
        }

        Object[] values = new Object[4];
        values[0] = name;
        values[1] = center;
        values[2] = size;
        values[3] = enlarge;

        return DeserializationUtils.deserializeRegion(values);
    }

    public boolean serialize(FlatDataModel flatDataModel) {
        File file = flatDataModel.loadCustomFile(BasicType.REGION, region.getName());
        YamlWrapper pc = new YamlWrapper(file);

        pc.set("name", region.getName());
        pc.set("center", LocationUtils.toString(region.getCenter()));
        pc.set("size", region.getSize());
        pc.set("enlarge", region.getEnlarge());

        pc.save();
        return true;
    }

}
