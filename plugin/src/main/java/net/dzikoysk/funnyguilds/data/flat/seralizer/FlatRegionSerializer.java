package net.dzikoysk.funnyguilds.data.flat.seralizer;

import java.io.File;
import java.util.Optional;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.Location;

public final class FlatRegionSerializer {

    private FlatRegionSerializer() {
    }

    public static Optional<Region> deserialize(File file) {
        if (file.isDirectory()) {
            return Optional.empty();
        }

        YamlWrapper wrapper = new YamlWrapper(file);

        String name = wrapper.getString("name");
        String centerString = wrapper.getString("center");
        int size = wrapper.getInt("size");
        int enlarge = wrapper.getInt("enlarge");

        if (name == null || centerString == null) {
            FunnyGuilds.getPluginLogger().deserialize("Cannot deserialize region! Caused by: name/center is null");
            return Optional.empty();
        }

        Location center = LocationUtils.parseLocation(centerString);
        if (center == null) {
            FunnyGuilds.getPluginLogger().deserialize("Cannot deserialize region! Caused by: center is null");
            return Optional.empty();
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

    public static boolean serialize(Region region) {
        FlatDataModel dataModel = (FlatDataModel) FunnyGuilds.getInstance().getDataModel();

        return dataModel.persistYaml(region, values -> {
            values.put("name", region.getName());
            values.put("center", LocationUtils.toString(region.getCenter()));
            values.put("size", region.getSize());
            values.put("enlarge", region.getEnlargementLevel());
        });
    }

}
