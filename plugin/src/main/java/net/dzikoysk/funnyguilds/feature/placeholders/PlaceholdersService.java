package net.dzikoysk.funnyguilds.feature.placeholders;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PlaceholdersService<T> {

    Component format(
            @Nullable Object entity,
            Component text,
            T data
    );

    default Component format(
            Component text,
            T data
    ) {
        return this.format(
                null,
                text,
                data
        );
    }

}
