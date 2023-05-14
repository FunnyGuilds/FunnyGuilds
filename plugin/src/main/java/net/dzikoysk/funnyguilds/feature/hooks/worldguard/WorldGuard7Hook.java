package net.dzikoysk.funnyguilds.feature.hooks.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import org.bukkit.Location;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class WorldGuard7Hook extends WorldGuardHook {

    private WorldGuard worldGuard;
    private StateFlag noPointsFlag;
    private StateFlag noGuildsFlag;
    private StateFlag friendlyFireFlag;

    public WorldGuard7Hook(String name) {
        super(name);
    }

    @Override
    public HookInitResult earlyInit() {
        this.worldGuard = WorldGuard.getInstance();
        this.noPointsFlag = new StateFlag("fg-no-points", false);
        this.noGuildsFlag = new StateFlag("fg-no-guilds", false);
        this.friendlyFireFlag = new StateFlag("fg-friendly-fire", false);

        this.worldGuard.getFlagRegistry().register(this.noPointsFlag);
        this.worldGuard.getFlagRegistry().register(this.noGuildsFlag);
        this.worldGuard.getFlagRegistry().register(this.friendlyFireFlag);
        return HookInitResult.SUCCESS;
    }

    @Override
    public boolean isInNonPointsRegion(Location location) {
        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return false;
        }

        return PandaStream.of(regionSet.get().getRegions())
                .find(region -> region.getFlag(this.noPointsFlag) == StateFlag.State.ALLOW)
                .isPresent();
    }

    @Override
    public boolean isInNonGuildsRegion(Location location) {
        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return false;
        }

        return PandaStream.of(regionSet.get().getRegions())
                .find(region -> region.getFlag(this.noGuildsFlag) == StateFlag.State.ALLOW)
                .isPresent();
    }

    @Override
    public boolean isInFriendlyFireRegion(Location location) {
        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return false;
        }

        return PandaStream.of(regionSet.get().getRegions())
                .find(region -> region.getFlag(this.friendlyFireFlag) == StateFlag.State.ALLOW)
                .isPresent();
    }

    @Override
    public boolean isInIgnoredRegion(Location location) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return false;
        }

        return PandaStream.of(regionSet.get().getRegions())
                .find(region -> config.assistsRegionsIgnored.contains(region.getId()))
                .isPresent();
    }

    @Override
    public boolean isInRegion(Location location) {
        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return false;
        }

        return regionSet.get().size() != 0;
    }

    @Override
    public Option<ApplicableRegionSet> getRegionSet(Location location) {
        if (location == null || this.worldGuard == null) {
            return Option.none();
        }

        RegionManager regionManager = this.worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
        if (regionManager == null) {
            return Option.none();
        }

        return Option.of(regionManager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ())));
    }

    @Override
    public List<String> getRegionNames(Location location) {
        Option<ApplicableRegionSet> regionSet = this.getRegionSet(location);
        if (regionSet.isEmpty()) {
            return Collections.emptyList();
        }

        return PandaStream.of(regionSet.get().getRegions())
                .map(ProtectedRegion::getId)
                .toList();
    }
}
