package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.SimpleResolver;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import panda.std.Option;

public abstract class Placeholders<T, P extends Placeholders<T, P>> {

    protected final Map<String, Placeholder<T>> placeholders = new ConcurrentHashMap<>();

    public abstract P create();

    public Map<String, Placeholder<T>> getPlaceholders() {
        return new HashMap<>(this.placeholders);
    }

    public Option<Placeholder<T>> getPlaceholder(String name) {
        return Option.of(this.placeholders.get(name));
    }

    public P property(String name, Placeholder<T> placeholder) {
        P copy = this.create();
        copy.placeholders.putAll(this.placeholders);
        copy.placeholders.put(name, placeholder);

        return copy;
    }

    public P property(String name, MonoResolver<T> resolver) {
        return this.property(name, new Placeholder<>(resolver));
    }

    public P property(String name, SimpleResolver resolver) {
        return this.property(name, object -> resolver.resolve());
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

    public <M> P map(Placeholders<M, ?> toMap, Function<String, String> nameMapper, BiFunction<T, Placeholder<M>, Object> dataMapper) {
        P copy = this.create();
        copy.placeholders.putAll(this.placeholders);

        toMap.getPlaceholders().forEach((key, placeholder) -> {
            copy.placeholders.put(nameMapper.apply(key), new Placeholder<>(data -> dataMapper.apply(data, placeholder)));
        });

        return copy;
    }

    public <M> P map(Placeholders<M, ?> toMap, Supplier<M> dataSupplier, Function<String, String> nameMapper) {
        return this.map(toMap, nameMapper, (data, placeholder) -> placeholder.getRaw(dataSupplier.get()));
    }

    public <M> P map(Placeholders<M, ?> toMap, Supplier<M> dataSupplier) {
        return this.map(toMap, dataSupplier, name -> name);
    }

    /**
     * Format text with raw placeholders
     *
     * @param text text to format
     * @param data data to use to formatting
     * @return formatted text
     */
    public String format(String text, T data) {
        return this.toFormatter(data).format(text);
    }

    public FunnyFormatter toFormatter(T data) {
        return this.toCustomFormatter(data, "", "", name -> name);
    }

    /**
     * Format text with variable format placeholders (e.g. {NAME})
     *
     * @param text text to format
     * @param data data to use to formatting
     * @return formatted text
     */
    public String formatVariables(String text, T data) {
        return this.toVariablesFormatter(data).format(text);
    }

    public FunnyFormatter toVariablesFormatter(T data) {
        return this.toCustomFormatter(data, "{", "}", String::toUpperCase);
    }

    /**
     * Format text with custom format placeholders
     *
     * @param text text to format
     * @param data data to use to formatting
     * @param prefix prefix to use before placeholders (for eg. "{")
     * @param suffix suffix to use after placeholders (for eg. "}")
     * @param nameModifier function to modify placeholder name (for eg. upper case)
     * @return formatted text
     */
    public String formatCustom(String text, T data, String prefix, String suffix, Function<String, String> nameModifier) {
        return this.toCustomFormatter(data, prefix, suffix, nameModifier).format(text);
    }

    public FunnyFormatter toCustomFormatter(T data, String prefix, String suffix, Function<String, String> nameModifier) {
        FunnyFormatter formatter = new FunnyFormatter();
        this.placeholders.forEach((key, placeholder) -> formatter.register(prefix + nameModifier.apply(key) + suffix, placeholder.get(data)));
        return formatter;
    }

}
