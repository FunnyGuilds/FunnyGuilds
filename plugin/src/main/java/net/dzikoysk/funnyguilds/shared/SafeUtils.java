package net.dzikoysk.funnyguilds.shared;

import net.dzikoysk.funnyguilds.FunnyGuilds;

public final class SafeUtils {

    private SafeUtils() {
    }

    private static void reportUnsafe(Throwable th) {
        FunnyGuilds.getPluginLogger().error("Something went wrong while handling unsafe", th);
    }

    public static <T> T safeInit(SafeInitializer<T> initializer) {
        try {
            return initializer.initialize();
        }
        catch (Exception e) {
            reportUnsafe(e);
            return null;
        }
    }

    @FunctionalInterface
    public interface SafeInitializer<T> {
        T initialize() throws Exception;
    }

}
