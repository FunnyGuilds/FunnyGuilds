package net.dzikoysk.funnyguilds.util.commons.bukkit;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.nms.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

public final class MinecraftServerUtils {

    private static final DecimalFormat FORMAT = new DecimalFormat("##.##");

    private static Object serverInstance;
    private static Field  tpsField;

    static {
        try {
            Class<?> minecraftServerClass = Reflections.getNMSClass("MinecraftServer");
            serverInstance = Reflections.getMethod(Reflections.getNMSClass("MinecraftServer"), "getServer").invoke(null);
            tpsField = minecraftServerClass.getDeclaredField("recentTps");
        }
        catch (IllegalAccessException | InvocationTargetException ex) {
            FunnyGuilds.getPluginLogger().error("Could not initialize MinecraftServerUtils", ex);
        }
        catch (NoSuchFieldException noSuchFieldException) {
            tpsField = null;
        }
    }

    private MinecraftServerUtils() {}

    // 0 = last 1 min, 1 = last 5 min, 2 = last 15min
    public static String getFormattedTPS(int last) {
        try {
            return tpsField != null ? FORMAT.format(Math.min(20.0D, ((double[]) tpsField.get(serverInstance))[last])) : "N/A";
        }
        catch (IllegalAccessException illegalAccessException) {
            FunnyGuilds.getPluginLogger().error("Could not retrieve recent TPS", illegalAccessException);
            return null;
        }
    }

    public static double getRecentTPS(int last) {
        try {
            return tpsField != null ? Math.min(20.0D, ((double[]) tpsField.get(serverInstance))[last]) : - 1.0;
        }
        catch (IllegalAccessException illegalAccessException) {
            FunnyGuilds.getPluginLogger().error("Could not retrieve recent TPS", illegalAccessException);
            return - 1.0;
        }
    }

}
