package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface SimpleResolver extends LocaleSimpleResolver {

    Object resolve();

    @Override
    default Object resolve(@Nullable Object entity) {
        return this.resolve();
    }

}
