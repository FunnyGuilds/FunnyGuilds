package net.dzikoysk.funnyguilds.feature.placeholders.placeholder;

import java.util.Objects;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleMonoResolver;
import org.jetbrains.annotations.Nullable;

public class Placeholder<T> {

    private final LocaleMonoResolver<T> resolver;

    public Placeholder(LocaleMonoResolver<T> resolver) {
        this.resolver = resolver;
    }

    public Object getRaw(@Nullable Object entity, T data) {
        return this.resolver.resolve(entity, data);
    }

    public String get(@Nullable Object entity, T data) {
        return Objects.toString(this.getRaw(entity, data), "");
    }

}
