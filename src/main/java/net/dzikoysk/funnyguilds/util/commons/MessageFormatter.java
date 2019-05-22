package net.dzikoysk.funnyguilds.util.commons;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

public class MessageFormatter {

    private final Map<String, Supplier<?>> placeholders;

    public MessageFormatter(Map<String, Supplier<?>> placeholders) {
        this.placeholders = placeholders;
    }

    public MessageFormatter() {
        this(new LinkedHashMap<>());
    }

    public String format(String message) {
        for (Map.Entry<String, Supplier<?>> placeholderEntry : placeholders.entrySet()) {
            String key = placeholderEntry.getKey();
            Object value = placeholderEntry.getValue().get();

            if (!message.contains(key)) {
                continue;
            }

            message = StringUtils.replace(message, key, value != null ? value.toString() : "<value not specified>");
        }

        return message;
    }

    public MessageFormatter register(String placeholder, Object value) {
        return register(placeholder, value::toString);
    }

    public MessageFormatter register(String placeholder, Supplier<?> value) {
        this.placeholders.put(placeholder, value);
        return this;
    }

    public MessageFormatter fork() {
        return new MessageFormatter(new LinkedHashMap<>(this.placeholders));
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String placeholder) {
        Supplier<?> supplier = placeholders.get(placeholder);
        return (T) supplier.get();
    }

}