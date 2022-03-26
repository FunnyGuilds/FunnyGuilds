package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MinecraftServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class SimplePlaceholders<T> extends Placeholders<T, SimplePlaceholders<T>> {

    public static final SimplePlaceholders<Object> SIMPLE;
    public static final SimplePlaceholders<LocalDateTime> TIME;
    public static final SimplePlaceholders<String> ONLINE;

    private static final Locale POLISH_LOCALE = new Locale("pl", "PL");

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();

        SIMPLE = new SimplePlaceholders<>()
                .property("tps", MinecraftServerUtils::getFormattedTPS)
                .property("online", () -> Bukkit.getOnlinePlayers().size())
                .property("users", () -> plugin.getUserManager().countUsers())
                .property("guilds", () -> plugin.getGuildManager().countGuilds());

        TIME = new SimplePlaceholders<LocalDateTime>()
                .timeProperty("hour", LocalDateTime::getHour)
                .timeProperty("minute", LocalDateTime::getMinute)
                .timeProperty("second", LocalDateTime::getSecond)
                .timeProperty("day_of_week", time -> time.getDayOfWeek().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                .timeProperty("day_of_month", LocalDateTime::getDayOfMonth)
                .timeProperty("month", time -> time.getMonth().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                .timeProperty("month_number", LocalDateTime::getMonthValue)
                .timeProperty("year", LocalDateTime::getYear);

        ONLINE = new SimplePlaceholders<String>()
                .raw("<online>", () -> ChatColor.GREEN)
                .raw("</online>", end -> end);
    }

    public SimplePlaceholders<T> timeProperty(String name, MonoResolver<LocalDateTime> timeResolver) {
        return this.property(name, (data) -> {
            if (!(data instanceof LocalDateTime)) {
                return "";
            }
            LocalDateTime time = (LocalDateTime) data;
            return ChatUtils.appendDigit(Objects.toString(timeResolver.resolve(time)));
        });
    }

    @Override
    public SimplePlaceholders<T> create() {
        return new SimplePlaceholders<>();
    }

}
