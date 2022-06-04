package net.dzikoysk.funnyguilds.shared;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.jetbrains.annotations.Nullable;

public final class SafeUtils {

    private SafeUtils() {
    }

    @Nullable
    public static <T> T safeInit(SafeInitializer<T> initializer) {
        try {
            return initializer.initialize();
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Something went wrong while handling unsafe", exception);
            return null;
        }
    }

    @FunctionalInterface
    public interface SafeInitializer<T> {
        T initialize() throws Exception;
    }

}
