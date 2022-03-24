package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

@FunctionalInterface
public interface MonoResolver<T> {
    Object resolve(T data);
}
