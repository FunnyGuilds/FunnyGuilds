package net.dzikoysk.funnyguilds.guild;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class RegionManager {

    private final FunnyGuilds plugin;

    private final PluginConfiguration pluginConfiguration;
    private final DataModel dataModel;

    private final Map<String, Region> regionsMap = new ConcurrentHashMap<>();

    public RegionManager(FunnyGuilds plugin) {
        this.plugin = plugin;

        this.pluginConfiguration = plugin.getPluginConfiguration();
        this.dataModel = plugin.getDataModel();
    }

    public int countRegions() {
        return this.regionsMap.size();
    }

    public Set<Region> getRegions() {
        return new HashSet<>(this.regionsMap.values());
    }

    public Option<Region> findByName(String name, boolean ignoreCase) {
        return PandaStream.of(regionsMap.entrySet())
                .find(entry -> ignoreCase
                        ? entry.getValue().getName().equalsIgnoreCase(name)
                        : entry.getValue().getName().equals(name))
                .map(Map.Entry::getValue);
    }

    public Option<Region> findByName(String name) {
        return this.findByName(name, false);
    }

    public Option<Region> findRegionAtLocation(Location location) {
        return PandaStream.of(regionsMap.entrySet())
                .find(entry -> entry.getValue().isIn(location))
                .map(Map.Entry::getValue);
    }

    public boolean isInRegion(Location location) {
        return this.findRegionAtLocation(location).isPresent();
    }

    public boolean isNearRegion(Location center) {
        if(center == null) {
            return false;
        }

        int size = this.pluginConfiguration.regionSize;

        if (this.pluginConfiguration.enlargeItems != null) {
            size += (this.pluginConfiguration.enlargeItems.size() * this.pluginConfiguration.enlargeSize);
        }

        int requiredDistance = (2 * size) + this.pluginConfiguration.regionMinDistance;

        return PandaStream.of(this.regionsMap.values())
                .map(Region::getCenter)
                .filter(Objects::nonNull)
                .filterNot(regionCenter -> regionCenter.equals(center))
                .filter(regionCenter -> regionCenter.getWorld().equals(center.getWorld()))
                .find(regionCenter -> LocationUtils.flatDistance(regionCenter, center) < requiredDistance)
                .isPresent();
    }

    public void addRegion(Region region) {
        Validate.notNull(region, "region can't be null!");
        this.regionsMap.put(region.getName(), region);
    }

    public void removeRegion(Region region) {
        Validate.notNull(region, "region can't be null!");
        this.regionsMap.remove(region.getName());
    }

    public void deleteRegion(Region region) {
        Validate.notNull(region, "region can't be null!");

        if (this.dataModel instanceof FlatDataModel) {
            FlatDataModel dataModel = (FlatDataModel) this.dataModel;
            dataModel.getRegionFile(region).delete();
        }

        if (this.dataModel instanceof SQLDataModel) {
            DatabaseRegion.delete(region);
        }

        this.removeRegion(region);
    }

    public boolean regionExists(String name) {
        return this.findByName(name).isPresent();
    }

}
