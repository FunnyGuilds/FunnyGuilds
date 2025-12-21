package net.dzikoysk.funnyguilds.shared;

import org.jetbrains.annotations.Nullable;

public final class ExceptionUtils {

    private ExceptionUtils() {
    }

    @Nullable
    public static <T extends Throwable> T findCause(Throwable throwable, Class<T> type, int maxDepth) {
        Throwable current = throwable;
        for (int i = 0; i < maxDepth && current != null; i++) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            current = current.getCause();
        }
        return null;
    }

}
