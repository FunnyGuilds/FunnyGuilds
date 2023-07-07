package net.dzikoysk.funnyguilds.feature.placeholders;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PlaceholdersService<T> {

    String format(@Nullable Object entity, String text, T data);

    default String format(String text, T data) {
        return this.format(null, text, data);
    }

}
