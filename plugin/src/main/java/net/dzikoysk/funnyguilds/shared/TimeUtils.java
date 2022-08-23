package net.dzikoysk.funnyguilds.shared;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import org.jetbrains.annotations.Nullable;

public final class TimeUtils {

    private static final TimeDivision[] LONG_DATE_DIVISIONS = new TimeDivision[] {
            TimeDivision.YEAR, TimeDivision.MONTH, TimeDivision.DAY, TimeDivision.HOUR, TimeDivision.MINUTE, TimeDivision.SECOND
    };

    private TimeUtils() {
    }

    @Nullable
    public static Instant positiveOrNullInstant(long time) {
        if (time <= 0) {
            return null;
        }
        return Instant.ofEpochMilli(time);
    }

    public static long parseTime(String time) {
        StringBuilder tempNumber = new StringBuilder();
        long resultTime = 0L;

        char[] stringChars = time.toLowerCase().toCharArray();
        timeLoop:
        for (int i = 0; i < stringChars.length; i++) {
            char c = stringChars[i];
            if (c >= '0' && c <= '9') {
                tempNumber.append(c);
                continue;
            }

            for (TimeDivision timeDivision : TimeDivision.values()) {
                abbreviationLoop:
                for (String abbreviation : timeDivision.getAbbreviations()) {
                    if (i + abbreviation.length() > stringChars.length) {
                        continue;
                    }

                    char[] abbreviationChars = abbreviation.toCharArray();
                    for (int a = 0; a < abbreviationChars.length; a++) {
                        if (abbreviationChars[a] != stringChars[i + a]) {
                            continue abbreviationLoop;
                        }
                    }

                    char next = '0';
                    if (i + abbreviation.length() < stringChars.length) {
                        next = stringChars[i + abbreviation.length()];
                    }

                    if (next < '0' || next > '9') {
                        continue;
                    }

                    if (tempNumber.length() == 0) {
                        return 0L;
                    }

                    resultTime += Long.parseLong(tempNumber.toString()) * timeDivision.getMillis();
                    tempNumber.setLength(0);

                    i += abbreviation.length() - 1;
                    continue timeLoop;
                }
            }

            return 0L;
        }

        if (tempNumber.length() != 0) {
            return 0L;
        }

        return resultTime;
    }

    public static Duration parseTimeDuration(String time) {
        return Duration.ofMillis(parseTime(time));
    }

    public static String formatTime(long time, String delimiter, boolean shortForm) {
        if (time <= 0) {
            return TimeDivision.SECOND.getForm(0, shortForm);
        }

        LinkedHashMap<TimeDivision, Long> timeParts = new LinkedHashMap<>();
        for (TimeDivision division : LONG_DATE_DIVISIONS) {
            long divisionTime = time / division.getMillis();

            time -= divisionTime * division.getMillis();
            if (divisionTime <= 0) {
                continue;
            }
            timeParts.put(division, divisionTime);
        }
        return formatTimeParts(timeParts, delimiter, shortForm);
    }

    public static String formatTime(long time) {
        return formatTime(time, " ", false);
    }

    public static String formatTime(Duration duration) {
        return formatTime(duration.toMillis(), " ", false);
    }

    public static String formatTimeShort(long time) {
        return formatTime(time, " ", true);
    }

    public static String formatTimeShort(Duration duration) {
        return formatTimeShort(duration.toMillis());
    }

    public static String formatTimeSimple(Duration duration) {
        return String.format("%.2f", duration.toMillis() / 1000.0);
    }

    private static String formatTimeParts(LinkedHashMap<TimeDivision, Long> timeParts, String delimiter, boolean shortForm) {
        StringBuilder timeStringBuilder = new StringBuilder();
        timeParts.forEach((key, partValue) -> {
            if (partValue == 0) {
                return;
            }
            timeStringBuilder.append(delimiter).append(partValue).append(key.getForm(partValue, shortForm));
        });

        if (timeStringBuilder.length() == 0) {
            return "0" + TimeDivision.SECOND.getForm(0, shortForm);
        }

        return timeStringBuilder.substring(delimiter.length());
    }

}
