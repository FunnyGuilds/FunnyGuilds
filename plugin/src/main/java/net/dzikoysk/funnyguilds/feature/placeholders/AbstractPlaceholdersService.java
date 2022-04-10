package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
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

    public Set<String> getPlaceholdersKeys() {
        return PandaStream.of(this.placeholders.values())
                .flatMap(placeholders -> placeholders.getPlaceholders().keySet())
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
            text = placeholders.formatVariables(text, data);
        }
        return text;
    }

    public String formatCustom(String text, T data, String prefix, String suffix, Function<String, String> nameModifier) {
        for (P placeholders : this.placeholders.values()) {
            text = placeholders.formatCustom(text, data, prefix, suffix, nameModifier);
        }
        return text;
    }

}
