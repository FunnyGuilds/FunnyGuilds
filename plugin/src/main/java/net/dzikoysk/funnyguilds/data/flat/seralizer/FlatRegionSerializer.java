package net.dzikoysk.funnyguilds.data.flat.seralizer;

import java.io.File;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.Location;
import panda.std.Option;

public final class FlatRegionSerializer {

    private FlatRegionSerializer() {
    }

    public static Option<Region> deserialize(File file) {
        if (file.isDirectory()) {
            return Option.none();
        }

        YamlWrapper wrapper = new YamlWrapper(file);

        String name = wrapper.getString("name");
        String centerString = wrapper.getString("center");
        int size = wrapper.getInt("size");
        int enlarge = wrapper.getInt("enlarge");

        if (name == null || centerString == null) {
            FunnyGuilds.getPluginLogger().deserialize("Cannot deserialize region! Caused by: name/center is null");
            return Option.none();
        }

        Location center = LocationUtils.parseLocation(centerString);
        if (center == null) {
            FunnyGuilds.getPluginLogger().deserialize("Cannot deserialize region! Caused by: center is null");
            return Option.none();
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

        Option<File> fileOption = dataModel.getRegionFile(region);
        if (fileOption.isEmpty()) {
            return false;
        }

        File regionFile = fileOption.get();
        if (regionFile.isDirectory()) {
            return false;
        }

        YamlWrapper wrapper = new YamlWrapper(regionFile);
        wrapper.set("name", region.getName());
        wrapper.set("center", LocationUtils.toString(region.getCenter()));
        wrapper.set("size", region.getSize());
        wrapper.set("enlarge", region.getEnlargementLevel());

        wrapper.save();
        region.markUnchanged();

        return true;
    }

}
