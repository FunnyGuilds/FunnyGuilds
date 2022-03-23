package net.dzikoysk.funnyguilds.shared;

import java.util.Stack;
import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnyguilds.FunnyGuilds;

public final class TimeUtils {

    private TimeUtils() {
    }

    public static long parseTime(String string) {
        if (string == null || string.isEmpty()) {
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
                        FunnyGuilds.getPluginLogger().parser("Unknown number: " + value.toString());
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

        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days);

            if (days == 1) {
                sb.append(" dzien ");
            }
            else {
                sb.append(" dni ");
            }
        }

        if (hours > 0) {
            sb.append(hours);

            long last = hours % 10;
            long lastTwo = hours % 100;

            if (hours == 1) {
                sb.append(" godzine ");
            }
            else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                sb.append(" godziny ");
            }
            else {
                sb.append(" godzin ");
            }
        }

        if (minutes > 0) {
            sb.append(minutes);

            long last = minutes % 10;
            long lastTwo = minutes % 100;

            if (minutes == 1) {
                sb.append(" minute ");
            }
            else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                sb.append(" minuty ");
            }
            else {
                sb.append(" minut ");
            }
        }

        if (seconds > 0) {
            sb.append(seconds);

            long last = seconds % 10;
            long lastTwo = seconds % 100;

            if (seconds == 1) {
                sb.append(" sekunde ");
            }
            else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                sb.append(" sekundy ");
            }
            else {
                sb.append(" sekund ");
            }
        }

        return (sb.toString());
    }

}
