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
import panda.std.Option;
import panda.utilities.text.Formatter;

public abstract class Placeholders<T, P extends Placeholders<T, P>> {

    protected final Map<String, Placeholder<T>> placeholders = new ConcurrentHashMap<>();

    public abstract P create();

    public Map<String, Placeholder<T>> getPlaceholders() {
        return new HashMap<>(this.placeholders);
    }

    public Option<Placeholder<T>> getPlaceholder(String name) {
        return Option.of(this.placeholders.get(name));
    }

    public Option<Placeholder<T>> getPlaceholderByProperty(String name) {
        return Option.of(this.placeholders.get(rawToProperty(name)));
    }

    public P raw(String name, Placeholder<T> placeholder) {
        P copy = this.create();
        copy.placeholders.putAll(this.placeholders);
        copy.placeholders.put(name, placeholder);
        return copy;
    }

    public P raw(String name, MonoResolver<T> resolver) {
        return this.raw(name, new Placeholder<>(resolver));
    }

    public P raw(String name, SimpleResolver resolver) {
        return this.raw(name, object -> resolver.resolve());
    }

    public P raw(Map<String, Placeholder<T>> placeholders) {
        P copy = this.create();
        copy.placeholders.putAll(this.placeholders);
        copy.placeholders.putAll(placeholders);
        return copy;
    }

    public P property(String name, Placeholder<T> placeholder) {
        return this.raw(rawToProperty(name), placeholder);
    }

    public P property(String name, MonoResolver<T> resolver) {
        return this.property(name, new Placeholder<>(resolver));
    }

    public P property(String name, SimpleResolver resolver) {
        return this.property(name, object -> resolver.resolve());
    }

    public <M> P map(Placeholders<M, ?> toMap, Function<String, String> nameMapper, BiFunction<T, Placeholder<M>, Object> dataMapper) {
        P copy = this.create();
        copy.placeholders.putAll(this.placeholders);
        toMap.getPlaceholders().forEach((key, placeholder) ->
                copy.placeholders.put(nameMapper.apply(key), new Placeholder<>(data -> dataMapper.apply(data, placeholder))));
        return copy;
    }

    public <M> P map(Placeholders<M, ?> toMap, Supplier<M> dataSupplier, Function<String, String> nameMapper) {
        return this.map(toMap, nameMapper, (data, placeholder) -> placeholder.getRaw(dataSupplier.get()));
    }

    public <M> P map(Placeholders<M, ?> toMap, Supplier<M> dataSupplier) {
        return this.map(toMap, dataSupplier, name -> name);
    }

    public String format(String text, T data) {
        return this.toFormatter(data)
                .format(text);
    }

    public Formatter toFormatter(T data) {
        Formatter formatter = new Formatter();
        placeholders.forEach((key, placeholder) -> formatter.register(key, placeholder.get(data)));
        return formatter;
    }

    public static String rawToProperty(String rawName) {
        return "{" + rawName.toUpperCase() + "}";
    }

    public static String propertyToRaw(String propertyName) {
        return propertyName.substring(1, propertyName.length() - 1);
    }

}
