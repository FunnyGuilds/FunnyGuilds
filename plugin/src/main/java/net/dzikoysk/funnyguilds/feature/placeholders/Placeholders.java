package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Placeholders<T, P extends Placeholder<T>> {

    private final Map<String, P> placeholders = new ConcurrentHashMap<>();

    public Map<String, P> getPlaceholders() {
        return new HashMap<>(this.placeholders);
    }

    public P getPlaceholder(String name) {
        return this.placeholders.get(name);
    }

    public Placeholders<T, P> register(String name, P placeholder) {
        this.placeholders.put(name, placeholder);
        return this;
    }

    public Placeholders<T, P> register(Collection<String> names, P placeholder) {
        names.forEach(name -> this.placeholders.put(name, placeholder));
        return this;
    }

}
