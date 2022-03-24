package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

@FunctionalInterface
public interface PairResolver<T, U> {
    Object resolve(T dataFirst, U dataSecond);
}
