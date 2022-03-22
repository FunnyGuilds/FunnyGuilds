package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Placeholders<T> {

    private final Map<String, Placeholder<T>> placeholders = new ConcurrentHashMap<>();

    public Placeholders() {
    }

    public Map<String, Placeholder<T>> getPlaceholders() {
        return new HashMap<>(this.placeholders);
    }

    public Placeholder<T> get(String name) {
        return this.placeholders.get(name);
    }

    public Placeholders<T> register(String name, Placeholder<T> placeholder) {
        this.placeholders.put(name, placeholder);
        return this;
    }

    public String parse(String string, T object) {
        for (Entry<String, Placeholder<T>> placeholder : this.placeholders.entrySet()) {
            string = string.replace(placeholder.getKey(), placeholder.getValue().get(object, "Brak"));
        }
        return string;
    }

}
