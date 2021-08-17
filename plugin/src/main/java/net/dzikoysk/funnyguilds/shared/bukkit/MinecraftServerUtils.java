package net.dzikoysk.funnyguilds.shared.bukkit;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import java.text.DecimalFormat;

public final class MinecraftServerUtils {

    private static final DecimalFormat FORMAT = new DecimalFormat("##.##");

    private MinecraftServerUtils() {}

    // 0 = last 1 min, 1 = last 5 min, 2 = last 15min
    public static String getFormattedTPS() {
        return FORMAT.format(getTpsInLastMinute());
    }

    public static double getTpsInLastMinute() {
        return Math.min(20.0D, FunnyGuilds.getInstance().getNmsAccessor().getStatisticsAccessor().getTpsInLastMinute());
    }

    public static int getReloadCount() {
        return FunnyGuilds.getInstance().getNmsAccessor().getStatisticsAccessor().getReloadCount();
    }

}
