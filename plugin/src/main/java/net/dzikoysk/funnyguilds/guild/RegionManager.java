package net.dzikoysk.funnyguilds.guild;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseRegionSerializer;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;
import panda.std.Option;
import panda.std.Pair;
import panda.std.stream.PandaStream;

public class RegionManager {

    private final PluginConfiguration pluginConfiguration;
    private final Map<String, Region> regionsMap = new ConcurrentHashMap<>();

    public RegionManager(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    public int countRegions() {
        return this.regionsMap.size();
    }

    /**
     * Gets the copied set of regions.
     *
     * @return set of regions
     */
    public Set<Region> getRegions() {
        return new HashSet<>(this.regionsMap.values());
    }

    /**
     * Deletes all loaded regions data
     */
    public void clearRegions() {
        this.regionsMap.clear();
    }

    /**
     * Gets the region.
     *
     * @param name       the name of region (probably name of guild)
     * @param ignoreCase ignore the case of the tag
     * @return the guild
     */
    public Option<Region> findByName(String name, boolean ignoreCase) {
        Region foundRegion = this.regionsMap.get(name);

        if (foundRegion == null && ignoreCase) {
            foundRegion = PandaStream.of(this.regionsMap.values())
                    .find(region -> region.getName().equalsIgnoreCase(name))
                    .orNull();
        }

        return Option.of(foundRegion);
    }

    /**
     * Gets the region.
     *
     * @param name the name of region (probably name of guild)
     * @return the region
     */
    public Option<Region> findByName(String name) {
        return this.findByName(name, false);
    }

    /**
     * Gets the region.
     *
     * @param location the location of region
     * @return the region
     */
    public Option<Region> findRegionAtLocation(Location location) {
        return PandaStream.of(this.regionsMap.values()).find(region -> region.isIn(location));
    }

    /**
     * Checks if there is a region in the given location.
     *
     * @param location the location of region
     * @return if given location is in region
     */
    public boolean isInRegion(Location location) {
        return this.findRegionAtLocation(location).isPresent();
    }

    public boolean isAnyPlayerInRegion(Region region, Collection<UUID> ignoredUuids) {
        if (!this.pluginConfiguration.regionsEnabled) {
            return false;
        }

        FunnyBox box = region.toBox();
        return PandaStream.of(Bukkit.getOnlinePlayers())
                .filter(player -> !ignoredUuids.contains(player.getUniqueId()))
                .find(player -> box.contains(player.getLocation()))
                .isPresent();
    }

    public boolean isAnyPlayerInRegion(Region region) {
        return this.isAnyPlayerInRegion(region, Collections.emptySet());
    }

    public boolean isAnyUserInRegion(Region region, Collection<User> ignoredUsers) {
        return this.isAnyPlayerInRegion(region, PandaStream.of(ignoredUsers)
                .map(User::getUUID)
                .collect(Collectors.toSet()));
    }

    public boolean isAnyUserInRegion(Option<Region> regionOption, Collection<User> ignoredUsers) {
        return regionOption.map(region -> this.isAnyUserInRegion(region, ignoredUsers)).orElseGet(false);
    }

    /**
     * Checks if there is any region in area (used to check if it's possible to create a new guild in given location).
     *
     * @param center the center of region
     * @return if is region nearby
     */
    public boolean isNearRegion(Location center) {
        if (center == null) {
            return false;
        }

        int size = this.pluginConfiguration.regionSize;
        if (this.pluginConfiguration.enlargeItems != null) {
            size += (this.pluginConfiguration.enlargeItems.size() * this.pluginConfiguration.enlargeSize);
        }

        int requiredDistance = (2 * size) + this.pluginConfiguration.regionMinDistance;
        return PandaStream.of(this.regionsMap.values())
                .map(Region::getCenter)
                .filterNot(regionCenter -> regionCenter.equals(center))
                .filter(regionCenter -> regionCenter.getWorld().equals(center.getWorld()))
                .find(regionCenter -> LocationUtils.flatDistance(regionCenter, center) < requiredDistance)
                .isPresent();
    }

    public boolean isNearRegion(Option<Location> center) {
        return center.map(this::isNearRegion).orElseGet(false);
    }

    /**
     * Checks if given block is guild's heart.
     *
     * @param block block to check
     * @return if given block is guild's heart
     */
    public boolean isGuildHeart(Block block) {
        Pair<Material, Byte> md = this.pluginConfiguration.heart.createMaterial;
        if (md == null || block.getType() != md.getFirst()) {
            return false;
        }

        Location blockLocation = block.getLocation();
        return this.findRegionAtLocation(blockLocation)
                .map(region -> region.getHeart().contentEquals(blockLocation))
                .orElseGet(false);
    }

    /**
     * Add region to storage. If you think you should use this method you probably shouldn't.
     *
     * @param region region to add
     */
    public void addRegion(Region region) {
        Validate.notNull(region, "region can't be null!");
        this.regionsMap.put(region.getName(), region);
    }

    /**
     * Remove region from storage. If you think you should use this method you probably shouldn't - instead use {@link RegionManager#deleteRegion(DataModel, Region)}.
     *
     * @param region region to remove
     */
    public void removeRegion(Region region) {
        Validate.notNull(region, "region can't be null!");
        this.regionsMap.remove(region.getName());
    }

    /**
     * Delete region from storage and from database.
     *
     * @param region region to delete
     * @deprecated for removal in the future with database rework (GH-1402)
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public void deleteRegion(DataModel dataModel, Region region) {
        Validate.notNull(region, "region can't be null!");

        if (dataModel instanceof FlatDataModel) {
            ((FlatDataModel) dataModel).getRegionFile(region).peek(FunnyIOUtils::deleteFile);
        }

        if (dataModel instanceof SQLDataModel) {
            DatabaseRegionSerializer.delete((SQLDataModel) dataModel, region);
        }

        this.removeRegion(region);
    }

    /**
     * Checks if region with given name exists.
     *
     * @param name region name
     * @return if region exists
     */
    public boolean regionExists(String name) {
        return this.findByName(name).isPresent();
    }

}
