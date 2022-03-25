package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.SimpleResolver;
import net.dzikoysk.funnyguilds.shared.bukkit.MinecraftServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import panda.utilities.text.Formatter;

public class Placeholders<T> {

    public static final Placeholders<Object> SIMPLE;
    public static final Placeholders<LocalDateTime> TIME;
    public static final Placeholders<String> ONLINE;

    private static final Locale POLISH_LOCALE = new Locale("pl", "PL");

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();

        SIMPLE = new Placeholders<>()
                .property("tps", MinecraftServerUtils::getFormattedTPS)
                .property("online", () -> Bukkit.getOnlinePlayers().size())
                .property("users", () -> plugin.getUserManager().countUsers())
                .property("guilds", () -> plugin.getGuildManager().countGuilds());

        TIME = new Placeholders<LocalDateTime>()
                .property("hour", LocalDateTime::getHour)
                .property("minute", LocalDateTime::getMinute)
                .property("second", LocalDateTime::getSecond)
                .property("day_of_week", time -> time.getDayOfWeek().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                .property("day_of_month", LocalDateTime::getDayOfMonth)
                .property("month", time -> time.getMonth().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                .property("month_number", LocalDateTime::getMonthValue)
                .property("year", LocalDateTime::getYear);

        ONLINE = new Placeholders<String>()
                .raw("<online>", () -> ChatColor.GREEN)
                .raw("</online>", end -> end);
    }

    protected final Map<String, Placeholder<T>> placeholders = new ConcurrentHashMap<>();

    public Map<String, Placeholder<T>> getPlaceholders() {
        return new HashMap<>(this.placeholders);
    }

    public Placeholder<T> getPlaceholder(String name) {
        return this.placeholders.get(name);
    }

    public Placeholders<T> raw(String name, Placeholder<T> placeholder) {
        return this.copy(copy -> copy.placeholders.put(name, placeholder));
    }

    public Placeholders<T> raw(String name, MonoResolver<T> resolver) {
        return this.raw(name, new Placeholder<>(resolver));
    }

    public Placeholders<T> raw(String name, SimpleResolver resolver) {
        return this.raw(name, object -> resolver.resolve());
    }

    public Placeholders<T> property(String name, Placeholder<T> placeholder) {
        return this.raw("{" + name.toUpperCase() + "}", placeholder);
    }

    public Placeholders<T> property(String name, MonoResolver<T> resolver) {
        return this.property(name, new Placeholder<>(resolver));
    }

    public Placeholders<T> property(String name, SimpleResolver resolver) {
        return this.property(name, object -> resolver.resolve());
    }

    public String format(String text, T data) {
        for (Entry<String, Placeholder<T>> placeholder : this.placeholders.entrySet()) {
            text = text.replace(placeholder.getKey(), placeholder.getValue().get(data));
        }
        return text;
    }

    public Formatter toFormatter(T data) {
        Formatter formatter = new Formatter();
        placeholders.forEach((key, placeholder) -> formatter.register(key, placeholder.get(data)));
        return formatter;
    }

    public Placeholders<T> copy(Consumer<Placeholders<T>> andThen) {
        Placeholders<T> copy = new Placeholders<>();
        copy.placeholders.putAll(this.placeholders);
        andThen.accept(copy);
        return copy;
    }

    public static String getRawName(String name) {
        return name.replace("{", "")
                .replace("}", "");
    }

}
