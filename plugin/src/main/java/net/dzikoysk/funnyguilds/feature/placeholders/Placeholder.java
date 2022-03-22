package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Objects;

public interface Placeholder<T> {

    Object getRaw(T object);

    default String get(T object) {
        return Objects.toString(this.getRaw(object));
    }

    default String get(T object, String nullValue) {
        return Objects.toString(this.getRaw(object), nullValue);
    }

}
