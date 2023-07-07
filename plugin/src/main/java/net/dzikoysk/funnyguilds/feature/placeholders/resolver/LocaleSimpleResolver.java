package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface LocaleSimpleResolver extends Consumer<Object> {

    Object resolve(@Nullable Object entity);

    @Override
    default void accept(Object o) {
        this.resolve(o);
    }

}
