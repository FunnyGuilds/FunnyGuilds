package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.Placeholder;
import org.bukkit.plugin.java.JavaPlugin;
import panda.std.Option;
import panda.std.stream.PandaStream;
import panda.utilities.text.Joiner;

public abstract class AbstractPlaceholdersService<T, P extends Placeholders<T, P>> implements PlaceholdersService<T> {

    protected static final BiFunction<Collection<String>, String, String> JOIN_OR_DEFAULT = (list, listNoValue) -> list.isEmpty()
            ? listNoValue
            : Joiner.on(", ").join(list).toString();

    protected final Map<String, P> placeholders = new ConcurrentHashMap<>();

    public void register(JavaPlugin plugin, String name, P placeholders) {
        this.placeholders.put(plugin.getName().toLowerCase() + "_" + name.toLowerCase(), placeholders);
    }

    public Collection<P> getPlaceholders() {
        return this.placeholders.values();
    }

    public Map<String, P> getPlaceholdersMap() {
        return new HashMap<>(this.placeholders);
    }

    public Set<Map.Entry<String, Placeholder<T>>> getPlaceholdersEntries() {
        return PandaStream.of(this.placeholders.values())
                .flatMap(placeholders -> placeholders.placeholders.entrySet())
                .toSet();
    }

    public Option<Placeholder<T>> getPlaceholder(String name) {
        for (P placeholders : this.placeholders.values()) {
            Option<Placeholder<T>> placeholder = placeholders.getPlaceholder(name);
            if (placeholder.isPresent()) {
                return placeholder;
            }
        }
        return Option.none();
    }

    @Override
    public String format(String text, T data) {
        for (P placeholders : this.placeholders.values()) {
            text = this.format(placeholders, text, data);
        }
        return text;
    }

    protected String format(P placeholders, String text, T data) {
        if (placeholders == null) {
            return text;
        }
        return placeholders.formatVariables(text, data);
    }

}
