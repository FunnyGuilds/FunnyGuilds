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
        if (message == null || message.isEmpty()) {
            return "";
        }

        for (Pair<String, Supplier<?>> placeholderPair : this.placeholders) {
            message = format(message, placeholderPair.getFirst(), placeholderPair.getSecond());
        }

        return message;
    }

    public static String format(String message, String placeholder, Object value) {
        return format(message, placeholder, value::toString);
    }

    public static String format(String message, String placeholder, Supplier<?> valueSupplier) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        if (!message.contains(placeholder)) {
            return message;
        }

        Object value = valueSupplier.get();
        if (value == null) {
            throw new NullPointerException("Placeholder " + placeholder + " returns null value");
        }

        return StringUtils.replace(message, placeholder, Objects.toString(value));
    }

    public FunnyFormatter register(String placeholder, Object value) {
        return this.register(placeholder, value::toString);
    }

    public FunnyFormatter register(String placeholder, Supplier<?> valueSupplier) {
        this.placeholders.add(Pair.of(placeholder, valueSupplier));
        return this;
    }

    public static FunnyFormatter of(String placeholder, Object value) {
        return of(placeholder, value::toString);
    }

    public static FunnyFormatter of(String placeholder, Supplier<?> valueSupplier) {
        return new FunnyFormatter().register(placeholder, valueSupplier);
    }

}
