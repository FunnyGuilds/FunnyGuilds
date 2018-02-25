package net.dzikoysk.funnyguilds.util;

public final class SafeUtils {

    private static void reportUnsafe(Throwable t) {
        FunnyLogger.exception(t);
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
