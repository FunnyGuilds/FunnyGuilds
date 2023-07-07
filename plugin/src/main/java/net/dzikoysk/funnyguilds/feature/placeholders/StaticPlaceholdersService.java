package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import panda.std.stream.PandaStream;
import panda.utilities.text.Joiner;

public abstract class StaticPlaceholdersService<T, P extends Placeholders<T, P>> implements PlaceholdersService<T> {

    protected static final BiFunction<Collection<String>, String, String> JOIN_OR_DEFAULT =
            (list, listNoValue) -> list.isEmpty()
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
     * Format text with all placeholders.
     *
     * @param text text to format
     * @param data data to use to formatting
     * @return formatted text
     */
    @Override
    public String format(@Nullable Object entity, String text, T data) {
        for (P placeholders : this.placeholders.values()) {
            text = placeholders.formatVariables(entity, text, data);
        }
        return text;
    }

    public String formatCustom(@Nullable Object entity, @Nullable String text, T data, String prefix, String suffix, Function<String, String> nameModifier) {
        for (P placeholders : this.placeholders.values()) {
            text = placeholders.formatCustom(entity, text, data, prefix, suffix, nameModifier);
        }
        return text;
    }

    public String formatCustom(@Nullable String text, T data, String prefix, String suffix, Function<String, String> nameModifier) {
        return this.formatCustom(null, text, data, prefix, suffix, nameModifier);
    }

    public Set<FunnyFormatter> prepareReplacements(@Nullable Object entity, T data) {
        return PandaStream.of(this.placeholders.values())
                .map(placeholders -> placeholders.toVariablesFormatter(entity, data))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
