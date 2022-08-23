package net.dzikoysk.funnyguilds.shared;

import java.time.Duration;
import java.time.Instant;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.jetbrains.annotations.Nullable;

public final class TimeUtils {

    private TimeUtils() {
    }

    @Nullable
    public static Instant positiveOrNullInstant(long time) {
        if (time <= 0) {
            return null;
        }
        return Instant.ofEpochMilli(time);
    }

    public static Duration parseTimeDuration(String string) {
        return Duration.ofMillis(parseTime(string));
    }

    public static long parseTime(String string) {
        if (FunnyStringUtils.isEmpty(string)) {
            return 0;
        }

        Stack<Character> type = new Stack<>();
        StringBuilder value = new StringBuilder();

        boolean calc = false;
        long time = 0;

        for (char c : string.toCharArray()) {
            switch (c) {
                case 'd':
                case 'h':
                case 'm':
                case 's':
                    if (!calc) {
                        type.push(c);
                    }

                    try {
                        long i = Integer.parseInt(value.toString());
                        switch (type.pop()) {
                            case 'd':
                                time += i * 86400000L;
                                break;
                            case 'h':
                                time += i * 3600000L;
                                break;
                            case 'm':
                                time += i * 60000L;
                                break;
                            case 's':
                                time += i * 1000L;
                                break;
                        }
                    }
                    catch (NumberFormatException e) {
                        FunnyGuilds.getPluginLogger().parser("Unknown number: " + value);
                        return time;
                    }

                    type.push(c);
                    calc = true;

                    break;
                default:
                    value.append(c);
                    break;
            }
        }

        return time;
    }

    public static String getDurationBreakdown(Duration duration) {
        return getDurationBreakdown(duration.toMillis());
    }

    public static String getDurationBreakdown(long millis) {
        if (millis == 0) {
            return "0";
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        if (days > 0) {
            millis -= TimeUnit.DAYS.toMillis(days);
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        if (hours > 0) {
            millis -= TimeUnit.HOURS.toMillis(hours);
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        if (minutes > 0) {
            millis -= TimeUnit.MINUTES.toMillis(minutes);
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        if (seconds > 0) {
            millis -= TimeUnit.SECONDS.toMillis(seconds);
        }

        StringBuilder durationBuilder = new StringBuilder();

        if (days > 0) {
            durationBuilder.append(days);

            if (days == 1) {
                durationBuilder.append(" dzien ");
            }
            else {
                durationBuilder.append(" dni ");
            }
        }

        if (hours > 0) {
            durationBuilder.append(hours);

            long last = hours % 10;
            long lastTwo = hours % 100;

            if (hours == 1) {
                durationBuilder.append(" godzine ");
            }
            else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                durationBuilder.append(" godziny ");
            }
            else {
                durationBuilder.append(" godzin ");
            }
        }

        if (minutes > 0) {
            durationBuilder.append(minutes);

            long last = minutes % 10;
            long lastTwo = minutes % 100;

            if (minutes == 1) {
                durationBuilder.append(" minute ");
            }
            else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                durationBuilder.append(" minuty ");
            }
            else {
                durationBuilder.append(" minut ");
            }
        }

        if (seconds > 0) {
            durationBuilder.append(seconds);

            long last = seconds % 10;
            long lastTwo = seconds % 100;

            if (seconds == 1) {
                durationBuilder.append(" sekunde ");
            }
            else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                durationBuilder.append(" sekundy ");
            }
            else {
                durationBuilder.append(" sekund ");
            }
        }

        return durationBuilder.toString();
    }

}
