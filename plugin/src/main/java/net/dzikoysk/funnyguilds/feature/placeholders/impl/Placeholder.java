package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import java.util.Objects;

public interface Placeholder<T> {

    Object getRaw(T data);

    default String get(T data) {
        return Objects.toString(this.getRaw(data));
    }

}
