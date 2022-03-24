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

public class Placeholders<T> {

    public static final Placeholders<String> ONLINE;

    private static final Locale POLISH_LOCALE = new Locale("pl", "PL");

    static {
        ONLINE = new Placeholders<String>()
                .raw("<online>", end -> ChatColor.GREEN)
                .raw("</online>", end -> end);
    }

    private final Map<String, Placeholder<T>> placeholders = new ConcurrentHashMap<>();

    public Map<String, Placeholder<T>> getPlaceholders() {
        return new HashMap<>(this.placeholders);
    }

    public Placeholder<T> getPlaceholder(String name) {
        return this.placeholders.get(name);
    }

    protected Placeholders<T> raw(String name, Placeholder<T> placeholder) {
        this.placeholders.put(name, placeholder);
        return this;
    }

    protected Placeholders<T> raw(String name, MonoResolver<T> resolver) {
        return this.raw(name, new Placeholder<>(resolver));
    }

    protected Placeholders<T> raw(Collection<String> names, Placeholder<T> placeholder) {
        names.forEach(name -> this.raw(name, placeholder));
        return this;
    }

    protected Placeholders<T> raw(Collection<String> names, MonoResolver<T> resolver) {
        names.forEach(name -> this.raw(name, resolver));
        return this;
    }

    protected Placeholders<T> property(String name, Placeholder<T> placeholder) {
        return this.raw("{" + name.toUpperCase() + "}", placeholder);
    }

    protected Placeholders<T> property(String name, MonoResolver<T> resolver) {
        return this.property(name, new Placeholder<>(resolver));
    }

    protected Placeholders<T> property(Collection<String> names, Placeholder<T> placeholder) {
        names.forEach(name -> this.property(name, placeholder));
        return this;
    }

    protected Placeholders<T> property(Collection<String> names, MonoResolver<T> resolve) {
        names.forEach(name -> this.property(name, resolve));
        return this;
    }

    public String format(String text, T data) {
        for (Entry<String, Placeholder<T>> placeholder : placeholders.entrySet()) {
            text = text.replace(placeholder.getKey(), placeholder.getValue().get(data));
        }
        return text;
    }

    public Formatter toFormatter(T data) {
        Formatter formatter = new Formatter();
        placeholders.forEach((key, placeholder) -> formatter.register(key, placeholder.get(data)));
        return formatter;
    }

    public static class SimplePlaceholders extends Placeholders<Object> {

        public static SimplePlaceholders SIMPLE;

        static {
            FunnyGuilds plugin = FunnyGuilds.getInstance();

            SIMPLE = new SimplePlaceholders()
                    .property("tps", MinecraftServerUtils::getFormattedTPS)
                    .property("online", () -> Bukkit.getOnlinePlayers().size())
                    .property("users", () -> plugin.getUserManager().countUsers())
                    .property("guilds", () -> plugin.getGuildManager().countGuilds());
        }

        protected SimplePlaceholders property(String name, SimpleResolver resolver) {
            this.property(name, new Placeholder<>(object -> resolver.resolve()));
            return this;
        }

    }

    public static class TimePlaceholders extends Placeholders<LocalDateTime> {

        public static TimePlaceholders TIME;

        static {
            TIME = new TimePlaceholders()
                    .property("hour", LocalDateTime::getHour)
                    .property("minute", LocalDateTime::getMinute)
                    .property("second", LocalDateTime::getSecond)
                    .property("day_of_week", time -> time.getDayOfWeek().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                    .property("day_of_month", LocalDateTime::getDayOfMonth)
                    .property("month", time -> time.getMonth().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                    .property("month_number", LocalDateTime::getMonthValue)
                    .property("year", LocalDateTime::getYear);
        }

        @Override
        protected TimePlaceholders property(String name, MonoResolver<LocalDateTime> resolver) {
            super.property(name, resolver);
            return this;
        }

    }

    public static String getRawName(String name) {
        return name.replace("{", "")
                .replace("}", "");
    }

}
