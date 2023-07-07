package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PairResolver<T, U> extends LocalePairResolver<T, U> {

    Object resolve(T firstData, U secondData);

    @Override
    default Object resolve(@Nullable Object entity, T firstData, U secondData) {
        return this.resolve(firstData, secondData);
    }

}
