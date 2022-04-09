package net.dzikoysk.funnyguilds.feature.placeholders.placeholder;

import java.util.Objects;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.SimpleResolver;

public class FallbackPlaceholder<T> extends Placeholder<T> {

    private final SimpleResolver fallbackResolver;

    public FallbackPlaceholder(MonoResolver<T> resolver, SimpleResolver fallbackResolver) {
        super(resolver);
        this.fallbackResolver = fallbackResolver;
    }

    @Override
    public Object getRaw(T data) {
        if (data == null) {
            return this.getRawFallback();
        }
        return super.getRaw(data);
    }

    public Object getRawFallback() {
        return this.fallbackResolver.resolve();
    }

    public String getFallback() {
        return Objects.toString(this.getRawFallback(), "");
    }

}
