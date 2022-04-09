package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import org.bukkit.plugin.java.JavaPlugin;
import panda.utilities.text.Joiner;

public abstract class AbstractPlaceholdersService<T, P extends Placeholders<T, P>> implements PlaceholdersService<T> {

    protected static final BiFunction<Collection<String>, String, String> JOIN_OR_DEFAULT = (list, listNoValue) -> list.isEmpty()
            ? listNoValue
            : Joiner.on(", ").join(list).toString();

    protected final Map<String, P> placeholders = new ConcurrentHashMap<>();

    public void resolve(JavaPlugin plugin, String name, P placeholders) {
        this.placeholders.put(plugin.getName().toLowerCase() + "_" + name.toLowerCase(), placeholders);
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
