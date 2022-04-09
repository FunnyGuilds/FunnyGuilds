package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class TimePlaceholdersService extends AbstractPlaceholdersService<OffsetDateTime, OffsetDateTimePlaceholders> {

    private static final Locale POLISH_LOCALE = new Locale("pl", "PL");

    public static OffsetDateTimePlaceholders createTimePlaceholders() {
        return new OffsetDateTimePlaceholders()
                .timeProperty("hour", OffsetDateTime::getHour)
                .timeProperty("minute", OffsetDateTime::getMinute)
                .timeProperty("second", OffsetDateTime::getSecond)
                .timeProperty("day_of_week", time -> time.getDayOfWeek().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                .timeProperty("day_of_month", OffsetDateTime::getDayOfMonth)
                .timeProperty("month", time -> time.getMonth().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                .timeProperty("month_number", OffsetDateTime::getMonthValue)
                .timeProperty("year", OffsetDateTime::getYear);
    }

}
