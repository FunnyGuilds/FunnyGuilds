package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
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

    private static SimplePlaceholders<Object> SIMPLE;
    private static SimplePlaceholders<OffsetDateTime> TIME;
    private static SimplePlaceholders<String> ONLINE;

    private static final Locale POLISH_LOCALE = new Locale("pl", "PL");

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();

        ONLINE = new SimplePlaceholders<String>()
                .raw("<online>", () -> ChatColor.GREEN)
                .raw("</online>", end -> end);
    }

    public SimplePlaceholders<T> timeProperty(String name, MonoResolver<OffsetDateTime> timeResolver) {
        return this.property(name, (data) -> {
            if (!(data instanceof OffsetDateTime)) {
                return "";
            }
            OffsetDateTime time = (OffsetDateTime) data;
            return ChatUtils.appendDigit(Objects.toString(timeResolver.resolve(time)));
        });
    }

    @Override
    public SimplePlaceholders<T> create() {
        return new SimplePlaceholders<>();
    }

    public static SimplePlaceholders<Object> getOrInstallSimplePlaceholders(FunnyGuilds plugin) {
        if (SIMPLE == null) {
            SIMPLE = new SimplePlaceholders<>()
                    .property("tps", MinecraftServerUtils::getFormattedTPS)
                    .property("online", () -> Bukkit.getOnlinePlayers().size())
                    .property("users", () -> plugin.getUserManager().countUsers())
                    .property("guilds", () -> plugin.getGuildManager().countGuilds());
        }
        return SIMPLE;
    }

    public static SimplePlaceholders<OffsetDateTime> getOrInstallTimePlaceholders() {
        if (TIME == null) {
            TIME = new SimplePlaceholders<OffsetDateTime>()
                    .timeProperty("hour", OffsetDateTime::getHour)
                    .timeProperty("minute", OffsetDateTime::getMinute)
                    .timeProperty("second", OffsetDateTime::getSecond)
                    .timeProperty("day_of_week", time -> time.getDayOfWeek().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                    .timeProperty("day_of_month", OffsetDateTime::getDayOfMonth)
                    .timeProperty("month", time -> time.getMonth().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                    .timeProperty("month_number", OffsetDateTime::getMonthValue)
                    .timeProperty("year", OffsetDateTime::getYear);
        }
        return TIME;
    }

    public static SimplePlaceholders<String> getOrInstallOnlinePlaceholders() {
        if (ONLINE == null) {
            ONLINE = new SimplePlaceholders<String>()
                    .raw("<online>", () -> ChatColor.GREEN)
                    .raw("</online>", end -> end);
        }
        return ONLINE;
    }

}
