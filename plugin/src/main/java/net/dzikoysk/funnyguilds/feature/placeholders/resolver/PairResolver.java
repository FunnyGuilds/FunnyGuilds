package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import java.util.function.BiFunction;

public interface PairResolver<T, U> extends BiFunction<T, U, Object> {
    Object resolve(T dataFirst, U dataSecond);

    @Override
    default Object apply(T dataFirst, U dataSecond) {
        return this.resolve(dataFirst, dataSecond);
    }
}
