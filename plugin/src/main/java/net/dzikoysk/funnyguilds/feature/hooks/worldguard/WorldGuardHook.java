package net.dzikoysk.funnyguilds.feature.hooks.worldguard;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import java.util.List;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import org.bukkit.Location;
import panda.std.Option;

public abstract class WorldGuardHook extends AbstractPluginHook {

    public WorldGuardHook(String name) {
        super(name);
    }

    public abstract boolean isInNonPointsRegion(Location location);

    public abstract boolean isInIgnoredRegion(Location location);

    public abstract boolean isInNonGuildsRegion(Location location);

    public abstract FriendlyFireStatus getFriendlyFireStatus(Location location);

    public abstract boolean isInRegion(Location location);

    public abstract Option<ApplicableRegionSet> getRegionSet(Location location);

    public abstract List<String> getRegionNames(Location location);

    public enum FriendlyFireStatus {
        ALLOW,
        DENY,
        INHERIT
    }

}
