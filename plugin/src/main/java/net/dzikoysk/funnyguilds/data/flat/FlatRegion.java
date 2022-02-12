package net.dzikoysk.funnyguilds.data.flat;

import java.io.File;
import net.dzikoysk.funnyguilds.Entity.EntityType;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.Location;

public class FlatRegion {

    private final Region region;

    public FlatRegion(Region region) {
        this.region = region;
    }

    public static Region deserialize(File file) {
        YamlWrapper wrapper = new YamlWrapper(file);
        String name = wrapper.getString("name");
        String centerString = wrapper.getString("center");
        int size = wrapper.getInt("size");
        int enlarge = wrapper.getInt("enlarge");

        if (name == null || centerString == null) {
            FunnyGuilds.getPluginLogger().error("Cannot deserialize region! Caused by: name/center is null");
            return null;
        }

        Location center = LocationUtils.parseLocation(centerString);
        if (center == null) {
            FunnyGuilds.getPluginLogger().error("Cannot deserialize region! Caused by: center is null");
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

        return DeserializationUtils.deserializeRegion(FunnyGuilds.getInstance().getRegionManager(), values);
    }

    public boolean serialize(FlatDataModel flatDataModel) {
        File file = flatDataModel.loadCustomFile(EntityType.REGION, region.getName());
        YamlWrapper wrapper = new YamlWrapper(file);

        wrapper.set("name", region.getName());
        wrapper.set("center", LocationUtils.toString(region.getCenterOption().getOrNull()));
        wrapper.set("size", region.getSize());
        wrapper.set("enlarge", region.getEnlarge());

        wrapper.save();
        return true;
    }

}
