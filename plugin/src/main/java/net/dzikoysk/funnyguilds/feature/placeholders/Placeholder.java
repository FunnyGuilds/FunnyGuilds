package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Objects;

public interface Placeholder<T> {

    Object getRaw(T object);

    default String get(T object) {
        return Objects.toString(this.getRaw(object), this.getFallback(object));
    }

    default Object getRawFallback(T object) {
        return "";
    }

    default String getFallback(T object) {
        return Objects.toString(this.getRawFallback(object));
    }

}
