package net.dzikoysk.funnyguilds.util.commons;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;

public final class SafeUtils {

    private static void reportUnsafe(Throwable t) {
        FunnyGuildsLogger.exception(t);
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

    private SafeUtils() {}
}
