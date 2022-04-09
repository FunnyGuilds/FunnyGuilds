package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractPlaceholdersService<T, P extends Placeholders<T, P>> implements PlaceholdersService<T> {

    protected final Map<String, P> placeholders = new ConcurrentHashMap<>();

    public void resolve(String text, P placeholders) {
        this.placeholders.put(text, placeholders);
    }

    @Override
    public String format(String text, T data) {
        for (P placeholders : this.placeholders.values()) {
            text = this.format(placeholders, text, data);
        }
        return text;
    }

    protected String format(P placeholders, String text, T data) {
        return placeholders.formatVariables(text, data);
    }

}
