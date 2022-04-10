package net.dzikoysk.funnyguilds.feature.placeholders.placeholder;

import java.util.Objects;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;

public class Placeholder<T> {

    private final MonoResolver<T> resolver;

    public Placeholder(MonoResolver<T> resolver) {
        this.resolver = resolver;
    }

    public Object getRaw(T data) {
        return this.resolver.resolve(data);
    }

    public String get(T data) {
        return Objects.toString(this.getRaw(data), "");
    }

}
