package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public interface FallbackPlaceholder<T> {

    Object getRawFallback(@Nullable T object);

    default String getFallback(@Nullable T object) {
        return Objects.toString(this.getRawFallback(object));
    }

}
