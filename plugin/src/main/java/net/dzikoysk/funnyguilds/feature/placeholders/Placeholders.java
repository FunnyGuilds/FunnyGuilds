package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleMonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleSimpleResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.SimpleResolver;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Format text with raw placeholders
     *
     * @param text text to format
     * @param data data to use to formatting
     * @return formatted text
     */
    public String format(@Nullable Object entity, String text, T data) {
        return this.toFormatter(entity, data).format(text);
    }

    public String format(String text, T data) {
        return this.format(null, text, data);
    }

    public FunnyFormatter toFormatter(@Nullable Object entity, T data) {
        return this.toCustomFormatter(entity, data, "", "", name -> name);
    }

    public FunnyFormatter toFormatter(T data) {
        return this.toFormatter(null, data);
    }

    /**
     * Format text with variable format placeholders (e.g. {NAME})
     *
     * @param text text to format
     * @param data data to use to formatting
     * @return formatted text
     */
    public String formatVariables(@Nullable Object entity, String text, T data) {
        return this.toVariablesFormatter(entity, data).format(text);
    }

    public String formatVariables(String text, T data) {
        return this.formatVariables(null, text, data);
    }

    public FunnyFormatter toVariablesFormatter(@Nullable Object entity, T data) {
        return this.toCustomFormatter(entity, data, "{", "}", name -> name.toUpperCase(Locale.ROOT));
    }

    public FunnyFormatter toVariablesFormatter(T data) {
        return this.toVariablesFormatter(null, data);
    }

    /**
     * Format text with custom format placeholders
     *
     * @param text         text to format
     * @param data         data to use to formatting
     * @param prefix       prefix to use before placeholders (for eg. "{")
     * @param suffix       suffix to use after placeholders (for eg. "}")
     * @param nameModifier function to modify placeholder name (for eg. upper case)
     * @return formatted text
     */
    public String formatCustom(@Nullable Object entity, String text, T data, String prefix, String suffix, Function<String, String> nameModifier) {
        return this.toCustomFormatter(entity, data, prefix, suffix, nameModifier).format(text);
    }

    public String formatCustom(String text, T data, String prefix, String suffix, Function<String, String> nameModifier) {
        return this.formatCustom(null, text, data, prefix, suffix, nameModifier);
    }

    public FunnyFormatter toCustomFormatter(@Nullable Object entity, T data, String prefix, String suffix, Function<String, String> nameModifier) {
        FunnyFormatter formatter = new FunnyFormatter();
        this.placeholders.forEach((key, placeholder) -> formatter.register(prefix + nameModifier.apply(key) + suffix, placeholder.get(entity, data)));
        return formatter;
    }

    public FunnyFormatter toCustomFormatter(T data, String prefix, String suffix, Function<String, String> nameModifier) {
        return this.toCustomFormatter(null, data, prefix, suffix, nameModifier);
    }

}
