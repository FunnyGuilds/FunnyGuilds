package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
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
                .property("hour", LocalDateTime::getHour)
                .property("minute", LocalDateTime::getMinute)
                .property("second", LocalDateTime::getSecond)
                .property("day_of_week", time -> time.getDayOfWeek().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                .property("day_of_month", LocalDateTime::getDayOfMonth)
                .property("month", time -> time.getMonth().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                .property("month_number", LocalDateTime::getMonthValue)
                .property("year", LocalDateTime::getYear);

        ONLINE = new SimplePlaceholders<String>()
                .raw("<online>", () -> ChatColor.GREEN)
                .raw("</online>", end -> end);
    }

    @Override
    public SimplePlaceholders<T> create() {
        return new SimplePlaceholders<>();
    }

}
