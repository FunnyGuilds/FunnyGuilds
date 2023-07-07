package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface LocaleMonoResolver<T> extends Function<T, Object> {

    Object resolve(@Nullable Object entity, T data);

    @Override
    default Object apply(T data) {
        return this.resolve(null, data);
    }

}
