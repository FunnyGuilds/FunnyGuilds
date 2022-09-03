package net.dzikoysk.funnyguilds.shared;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.TimeInflection.Case;
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

    public static String formatTime(MessageConfiguration messages, Duration duration, String delimiter, Case inflectionCase) {
        long time = duration.toMillis();
        if (time <= 0) {
            return messages.getInflection(TimeDivision.SECOND)
                    .map(inflection -> inflection.getFormatted(0, inflectionCase))
                    .orElseGet(0 + " " + TimeDivision.SECOND.name().toLowerCase());
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
        return formatTimeParts(messages, timeParts, delimiter, inflectionCase);
    }

    public static String formatTime(MessageConfiguration messages, Duration duration, Case inflectionCase) {
        return formatTime(messages, duration, " ", inflectionCase);
    }

    public static String formatTimeShort(MessageConfiguration messages, Duration duration) {
        return formatTime(messages, duration, Case.SHORT);
    }

    public static String formatTimeSimple(Duration duration) {
        return String.format("%.2f", duration.toMillis() / 1000.0);
    }

    private static String formatTimeParts(MessageConfiguration messages,LinkedHashMap<TimeDivision, Long> timeParts, String delimiter, Case inflectionCase) {
        StringBuilder timeStringBuilder = new StringBuilder();
        timeParts.forEach((key, partValue) -> {
            if (partValue == 0) {
                return;
            }
            timeStringBuilder.append(delimiter);
            timeStringBuilder.append(messages.getInflection(key)
                    .map(inflection -> inflection.getFormatted(partValue, inflectionCase))
                    .orElseGet(partValue + " " + key.name().toLowerCase()));
        });

        if (timeStringBuilder.length() == 0) {
            return messages.getInflection(TimeDivision.SECOND)
                    .map(inflection -> inflection.getFormatted(0, inflectionCase))
                    .orElseGet(0 + " " + TimeDivision.SECOND.name().toLowerCase());
        }

        return timeStringBuilder.substring(delimiter.length());
    }

}
