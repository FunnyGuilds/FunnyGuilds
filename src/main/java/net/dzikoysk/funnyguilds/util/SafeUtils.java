package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;

public class SafeUtils {

    private static void reportUnsafe(Throwable t) {
        FunnyGuilds.exception(t);
    }

    public static <T> T safeInit(SafeInitializer<T> initializer) {
        try {
            return initializer.initialize();
        } catch (Exception e) {
            reportUnsafe(e);
            return null;
        }
    }

    @FunctionalInterface
    public interface SafeInitializer<T> {
        T initialize() throws Exception;
    }
}
