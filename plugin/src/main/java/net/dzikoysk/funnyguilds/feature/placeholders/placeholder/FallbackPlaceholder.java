package net.dzikoysk.funnyguilds.feature.placeholders.placeholder;

import java.util.Locale;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleMonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleSimpleResolver;

public class FallbackPlaceholder<T> extends Placeholder<T> {

    private final LocaleSimpleResolver fallbackResolver;

    public FallbackPlaceholder(LocaleMonoResolver<T> resolver, LocaleSimpleResolver fallbackResolver) {
        super(resolver);
        this.fallbackResolver = fallbackResolver;
    }

    @Override
    public Object getRaw(Locale entity, T data) {
        if (data == null) {
            return this.getRawFallback(entity);
        }

        Object providedValue = super.getRaw(entity, data);
        if (providedValue != null) {
            return providedValue;
        }
        return this.getRawFallback(entity);
    }

    private Object getRawFallback(Locale entity) {
        return this.fallbackResolver.resolve(entity);
    }

}
