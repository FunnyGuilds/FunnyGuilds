package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;

public class TimePlaceholdersService extends AbstractPlaceholdersService<OffsetDateTime, OffsetDateTimePlaceholders> {

    public static OffsetDateTimePlaceholders createTimePlaceholders(PluginConfiguration pluginConfiguration) {
        ZoneId timeZone = pluginConfiguration.timeZone;
        Locale locale = pluginConfiguration.timeFormatLocale;

        return new OffsetDateTimePlaceholders(timeZone)
                .timeProperty("hour", OffsetDateTime::getHour)
                .timeProperty("minute", OffsetDateTime::getMinute)
                .timeProperty("second", OffsetDateTime::getSecond)
                .timeProperty("day_of_week", time -> time.getDayOfWeek().getDisplayName(TextStyle.FULL, locale))
                .timeProperty("day_of_month", OffsetDateTime::getDayOfMonth)
                .timeProperty("month", time -> time.getMonth().getDisplayName(TextStyle.FULL, locale))
                .timeProperty("month_number", OffsetDateTime::getMonthValue)
                .timeProperty("year", OffsetDateTime::getYear);
    }

}
