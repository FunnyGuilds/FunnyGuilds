package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleMonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.SimpleResolver;

public abstract class Placeholders<T, P extends Placeholders<T, P>> {

    protected final Map<String, Placeholder<T>> placeholders = new ConcurrentHashMap<>();

    public abstract P create();

    public P property(String name, Placeholder<T> placeholder) {
        P copy = this.create();
        copy.placeholders.putAll(this.placeholders);
        copy.placeholders.put(name, placeholder);
        return copy;
    }

    public P property(String name, LocaleMonoResolver<T> resolver) {
        return this.property(name, new Placeholder<>(resolver));
    }

    public P property(String name, MonoResolver<T> resolver) {
        return this.property(name, (entity, data) -> resolver.resolve(data));
    }

    public P property(String name, SimpleResolver resolver) {
        return this.property(name, (entity, data) -> resolver.resolve());
    }

    public P property(Map<String, Placeholder<T>> placeholders) {
        P copy = this.create();
        copy.placeholders.putAll(this.placeholders);
        copy.placeholders.putAll(placeholders);
        return copy;
    }

    public P property(P placeholders) {
        return this.property(placeholders.placeholders);
    }
    
    public Map<String, Placeholder<T>> getPlaceholders() {
        return Collections.unmodifiableMap(this.placeholders);
    }
}
