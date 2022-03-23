package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import java.util.Objects;

public interface Placeholder<T> {

    Object getRaw(T object);

    default String get(T object) {
        return Objects.toString(this.getRaw(object));
    }

}
