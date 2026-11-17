package net.dzikoysk.funnyguilds.guild;

import java.util.Optional;
import javax.annotation.Nullable;

public final class RegionUtils {

    private RegionUtils() {
    }

    public static String toString(@Nullable Region region) {
        return region != null
                ? region.getName()
                : "null";
    }

    public static String toString(Optional<Region> region) {
        return toString(region.orElse(null));
    }

}
