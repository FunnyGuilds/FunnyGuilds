package net.dzikoysk.funnyguilds.feature.hooks.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.World;
import panda.std.Option;

public class WorldGuard7Hook extends WorldGuardHook {

    private WorldGuard worldGuard;

    public WorldGuard7Hook(String name) {
        super(name);
    }

    @Override
    public HookInitResult earlyInit() {
        this.worldGuard = WorldGuard.getInstance();
        this.initFlags();
        return HookInitResult.SUCCESS;
    }

    @Override
    protected void registerFlags(Flag<?>... flags) {
        FlagRegistry flagRegistry = this.worldGuard.getFlagRegistry();
        for (Flag<?> flag : flags) {
            flagRegistry.register(flag);
        }
    }

    @Override
    public Option<ApplicableRegionSet> getRegionSet(Location location) {
        if (location == null || this.worldGuard == null) {
            return Option.none();
        }

        World world = location.getWorld();
        if (world == null) {
            return Option.none();
        }

        RegionManager regionManager = this.worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        if (regionManager == null) {
            return Option.none();
        }
        return Option.of(regionManager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ())));
    }

}
