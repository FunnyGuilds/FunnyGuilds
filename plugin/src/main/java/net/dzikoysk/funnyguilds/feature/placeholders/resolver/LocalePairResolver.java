package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import java.util.function.BiFunction;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface LocalePairResolver<T, U> extends BiFunction<T, U, Object> {

    Object resolve(@Nullable Object entity, T dataFirst, U dataSecond);

    @Override
    default Object apply(T dataFirst, U dataSecond) {
        return this.resolve(null, dataFirst, dataSecond);
    }

}
