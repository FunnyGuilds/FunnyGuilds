package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import java.util.function.Function;

public interface MonoResolver<T> extends Function<T, Object> {
    Object resolve(T data);

    @Override
    default Object apply(T data) {
        return this.resolve(data);
    }
}
