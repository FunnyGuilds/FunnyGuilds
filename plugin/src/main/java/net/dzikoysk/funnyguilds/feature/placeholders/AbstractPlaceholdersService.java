package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Collection;
import java.util.Locale;
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

    /**
     * Register placeholders set.
     *
     * @param plugin       plugin which register placeholders set
     * @param name         name of placeholders set
     * @param placeholders placeholders set
     */
    public void register(JavaPlugin plugin, String name, P placeholders) {
        this.placeholders.put(plugin.getName().toLowerCase(Locale.ROOT) + "_" + name.toLowerCase(Locale.ROOT), placeholders);
    }

    /**
     * @return all placeholders key of all sets
     */
    public Set<String> getPlaceholdersKeys() {
        return PandaStream.of(this.placeholders.values())
                .flatMap(placeholders -> placeholders.getPlaceholders().keySet())
                .toSet();
    }

    /**
     * Find placeholder by key in all placeholders sets.
     *
     * @param name key of placeholder
     * @return placeholder
     */
    public Option<Placeholder<T>> getPlaceholder(String name) {
        return PandaStream.of(this.placeholders.values())
                .map(value -> value.getPlaceholder(name))
                .filter(Option::isPresent)
                .head()
                .orElseGet(Option.none());
    }

    /**
     * Format text with all placeholders.
     *
     * @param text text to format
     * @param data data to use to formatting
     * @return formatted text
     */
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
