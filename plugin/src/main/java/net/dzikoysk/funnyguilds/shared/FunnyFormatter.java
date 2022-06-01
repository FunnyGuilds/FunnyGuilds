package net.dzikoysk.funnyguilds.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import panda.std.Pair;
import panda.utilities.StringUtils;

public final class FunnyFormatter {

    private final List<Pair<String, Supplier<?>>> placeholders;

    public FunnyFormatter() {
        this.placeholders = new ArrayList<>();
    }

    public String format(String message) {
        for (Pair<String, Supplier<?>> placeholderPair : this.placeholders) {
            String key = placeholderPair.getFirst();

            if (!message.contains(key)) {
                continue;
            }

            Object value = placeholderPair.getSecond().get();
            if (value == null) {
                throw new NullPointerException("Placeholder " + key + " returns null value");
            }

            message = StringUtils.replace(message, key, Objects.toString(value));
        }

        return message;
    }

    public FunnyFormatter register(String placeholder, Object value) {
        return this.register(placeholder, value::toString);
    }

    public FunnyFormatter register(String placeholder, Supplier<?> value) {
        this.placeholders.add(Pair.of(placeholder, value));
        return this;
    }

    public static FunnyFormatter of(String placeholder, Object value) {
        return of(placeholder, value::toString);
    }

    public static FunnyFormatter of(String placeholder, Supplier<?> value) {
        return new FunnyFormatter().register(placeholder, value);
    }

}
