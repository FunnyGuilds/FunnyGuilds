package net.dzikoysk.funnyguilds.feature.placeholders.placeholder;

import java.util.Objects;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleMonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleSimpleResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.SimpleResolver;
import org.jetbrains.annotations.Nullable;

public class FallbackPlaceholder<T> extends Placeholder<T> {

    private final LocaleSimpleResolver fallbackResolver;

    public FallbackPlaceholder(LocaleMonoResolver<T> resolver, LocaleSimpleResolver fallbackResolver) {
        super(resolver);
        this.fallbackResolver = fallbackResolver;
    }

    @Override
    public Object getRaw(@Nullable Object entity, T data) {
        if (data == null) {
            return this.getRawFallback(entity);
        }

        return super.getRaw(entity, data);
    }

    public Object getRawFallback(@Nullable Object entity) {
        return this.fallbackResolver.resolve(entity);
    }

}
