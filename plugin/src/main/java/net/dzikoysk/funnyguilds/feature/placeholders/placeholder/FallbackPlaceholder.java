package net.dzikoysk.funnyguilds.feature.placeholders.placeholder;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public interface FallbackPlaceholder<T> {

    Object getRawFallback(@Nullable T data);

    default String getFallback(@Nullable T data) {
        return Objects.toString(this.getRawFallback(data));
    }

}
