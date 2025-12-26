package net.dzikoysk.funnyguilds.feature.placeholders.placeholder;

import java.util.Locale;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleMonoResolver;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.kyori.adventure.text.Component;

public class Placeholder<T> {

    private final LocaleMonoResolver<T> resolver;

    public Placeholder(LocaleMonoResolver<T> resolver) {
        this.resolver = resolver;
    }

    public Object getRaw(Locale entity, T data) {
        return ComponentUtil.toComponent(this.resolver.resolve(entity, data));
    }

    public Component get(Locale entity, T data) {
        return ComponentUtil.toComponent(this.getRaw(entity, data), Component.empty());
    }

}
