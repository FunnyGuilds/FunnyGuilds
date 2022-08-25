package net.dzikoysk.funnyguilds.shared;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import net.dzikoysk.funnyguilds.shared.TimeDivision.Case;
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

    public static Duration parseTime(String time) {
        StringBuilder tempNumber = new StringBuilder();
        long resultTime = 0;

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
                        return Duration.ofMillis(0);
                    }

                    resultTime += Long.parseLong(tempNumber.toString()) * timeDivision.getMillis();
                    tempNumber.setLength(0);

                    i += abbreviation.length() - 1;
                    continue timeLoop;
                }
            }

            return Duration.ofMillis(0);
        }

        if (tempNumber.length() != 0) {
            return Duration.ofMillis(0);
        }

        return Duration.ofMillis(resultTime);
    }

    public static String formatTime(Duration duration, String delimiter, Case inflectionCase) {
        long time = duration.toMillis();
        if (time <= 0) {
            return TimeDivision.SECOND.getFormatted(0, inflectionCase);
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
        return formatTimeParts(timeParts, delimiter, inflectionCase);
    }

    public static String formatTime(Duration duration, Case inflectionCase) {
        return formatTime(duration, " ", inflectionCase);
    }

    public static String formatTimeShort(Duration duration) {
        return formatTime (duration, Case.SHORT);
    }

    public static String formatTimeSimple(Duration duration) {
        return String.format("%.2f", duration.toMillis() / 1000.0);
    }

    private static String formatTimeParts(LinkedHashMap<TimeDivision, Long> timeParts, String delimiter, Case form) {
        StringBuilder timeStringBuilder = new StringBuilder();
        timeParts.forEach((key, partValue) -> {
            if (partValue == 0) {
                return;
            }
            timeStringBuilder.append(delimiter).append(key.getFormatted(partValue, form));
        });

        if (timeStringBuilder.length() == 0) {
            return TimeDivision.SECOND.getFormatted(0, form);
        }

        return timeStringBuilder.substring(delimiter.length());
    }

}
