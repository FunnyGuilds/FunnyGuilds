package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.SimpleResolver;
import net.dzikoysk.funnyguilds.shared.bukkit.MinecraftServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import panda.utilities.text.Formatter;

public class Placeholders<T, P extends Placeholder<T>> {

    public static final Placeholders<String, Placeholder<String>> ONLINE;

    private static final Locale POLISH_LOCALE = new Locale("pl", "PL");

    static {
        ONLINE = new Placeholders<String, Placeholder<String>>()
                .raw("<online>", end -> ChatColor.GREEN)
                .raw("</online>", end -> end);
    }

    private final Map<String, P> placeholders = new ConcurrentHashMap<>();

    public Map<String, P> getPlaceholders() {
        return new HashMap<>(this.placeholders);
    }

    public P getPlaceholder(String name) {
        return this.placeholders.get(name);
    }

    protected Placeholders<T, P> raw(String name, P placeholder) {
        this.placeholders.put(name, placeholder);
        return this;
    }

    protected Placeholders<T, P> raw(Collection<String> names, P placeholder) {
        names.forEach(name -> this.raw(name, placeholder));
        return this;
    }

    protected Placeholders<T, P> property(String name, P placeholder) {
        return this.raw("{" + name.toUpperCase() + "}", placeholder);
    }

    protected Placeholders<T, P> property(Collection<String> names, P placeholder) {
        names.forEach(name -> this.property(name, placeholder));
        return this;
    }

    public String format(String text, T data) {
        for (Entry<String, P> placeholder : placeholders.entrySet()) {
            text = text.replace(placeholder.getKey(), placeholder.getValue().get(data));
        }
        return text;
    }

    public Formatter toFormatter(T data) {
        Formatter formatter = new Formatter();
        placeholders.forEach((key, placeholder) -> formatter.register(key, placeholder.get(data)));
        return formatter;
    }

    public static class SimplePlaceholders extends Placeholders<Object, Placeholder<Object>> {

        public static SimplePlaceholders SIMPLE;

        static {
            FunnyGuilds plugin = FunnyGuilds.getInstance();

            SIMPLE = new SimplePlaceholders()
                    .simpleProperty("tps", MinecraftServerUtils::getFormattedTPS)
                    .simpleProperty("online", () -> Bukkit.getOnlinePlayers().size())
                    .simpleProperty("users", () -> plugin.getUserManager().countUsers())
                    .simpleProperty("guilds", () -> plugin.getGuildManager().countGuilds());
        }

        protected SimplePlaceholders simpleProperty(String name, SimpleResolver resolver) {
            this.property(name, object -> resolver.resolve());
            return this;
        }

    }

    public static class TimePlaceholders extends Placeholders<LocalDateTime, Placeholder<LocalDateTime>> {

        public static TimePlaceholders TIME;

        static {
            TIME = new TimePlaceholders()
                    .timeProperty("hour", LocalDateTime::getHour)
                    .timeProperty("minute", LocalDateTime::getMinute)
                    .timeProperty("second", LocalDateTime::getSecond)
                    .timeProperty("day_of_week", time -> time.getDayOfWeek().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                    .timeProperty("day_of_month", LocalDateTime::getDayOfMonth)
                    .timeProperty("month", time -> time.getMonth().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                    .timeProperty("month_number", LocalDateTime::getMonthValue)
                    .timeProperty("year", LocalDateTime::getYear);
        }

        protected TimePlaceholders timeProperty(String name, MonoResolver<LocalDateTime> resolver) {
            this.property(name, resolver::resolve);
            return this;
        }

    }

}
