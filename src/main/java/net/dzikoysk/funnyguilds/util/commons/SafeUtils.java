package net.dzikoysk.funnyguilds.util.commons;

import net.dzikoysk.funnyguilds.FunnyGuilds;

public final class SafeUtils {

    private static void reportUnsafe(Throwable th) {
        FunnyGuilds.getInstance().getPluginLogger().error("Something went wront while handling unsafe", th);
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
