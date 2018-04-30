package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

public final class Ticker {

    private static final DecimalFormat FORMAT = new DecimalFormat("##.##");

    private static Object serverInstance;
    private static Field tpsField;

    static {
        try {
            Class<?> minecraftServerClass = Reflections.getNMSClass("MinecraftServer");
            serverInstance = Reflections.getMethod(Reflections.getNMSClass("MinecraftServer"), "getServer").invoke(null);
            tpsField = minecraftServerClass.getDeclaredField("recentTps");
        } catch (IllegalAccessException | InvocationTargetException ex) {
            FunnyLogger.exception(ex.getMessage(), ex.getStackTrace());
        } catch (NoSuchFieldException ex) {
            tpsField = null;
        }
    }

    // 0 = last 1 min, 1 = last 5 min, 2 = last 15min
    public static String getRecentTPS(int last) {
        try {
            return tpsField != null ? FORMAT.format(Math.min(20.0D, ((double[]) tpsField.get(serverInstance))[last])) : "N/A";
        } catch (IllegalAccessException ex) {
            FunnyLogger.exception(ex.getMessage(), ex.getStackTrace());
            return null;
        }
    }

    private Ticker() {}
    
}
