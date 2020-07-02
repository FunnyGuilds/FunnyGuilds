package net.dzikoysk.funnyguilds.hook.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

public class WorldGuard7Hook implements WorldGuardHook {

    private WorldGuard worldGuard;
    private StateFlag noPointsFlag;

    @Override
    public void init() {
        worldGuard = WorldGuard.getInstance();
        noPointsFlag = new StateFlag("fg-no-points", false);

        worldGuard.getFlagRegistry().register(noPointsFlag);
    }

    @Override
    public boolean isInNonPointsRegion(Location location) {
        if (! isInRegion(location)) {
            return false;
        }

        for (ProtectedRegion region : getRegionSet(location)) {
            if (region.getFlag(noPointsFlag) == StateFlag.State.ALLOW) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isInIgnoredRegion(Location location) {
        if (! isInRegion(location)) {
            return false;
        }

        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        return getRegionSet(location).getRegions()
                .stream()
                .anyMatch(region -> config.assistsRegionsIgnored.contains(region.getId()));
    }

    @Override
    public boolean isInRegion(Location location) {
        ApplicableRegionSet regionSet = getRegionSet(location);
        if (regionSet == null) {
            return false;
        }

        return regionSet.size() != 0;
    }

    @Override
    public ApplicableRegionSet getRegionSet(Location location) {
        if (location == null || worldGuard == null) {
            return null;
        }

        RegionManager regionManager = worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
        if (regionManager == null) {
            return null;
        }

        return regionManager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()));
    }

    @Override
    public List<String> getRegionNames(Location location) {
        ApplicableRegionSet regionSet = getRegionSet(location);
        return regionSet == null ? null : regionSet.getRegions().stream()
                .map(ProtectedRegion::getId)
                .collect(Collectors.toList());
    }
}
