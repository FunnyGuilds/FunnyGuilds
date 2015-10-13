package net.dzikoysk.funnyguilds.util;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String getDurationBreakdown(long millis) {
        if (millis == 0)
            return "0";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        if (days > 0)
            millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        if (hours > 0)
            millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        if (minutes > 0)
            millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        if (seconds > 0)
            millis -= TimeUnit.SECONDS.toMillis(seconds);

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days);
            long i = days % 10;
            if (i == 1)
                sb.append(" dzien ");
            else
                sb.append(" dni ");
        }
        if (hours > 0) {
            sb.append(hours);
            long i = hours % 10;
            if (i == 1)
                sb.append(" godzine ");
            else if (i < 5)
                sb.append(" godziny ");
            else
                sb.append(" godzin ");
        }
        if (minutes > 0) {
            sb.append(minutes);
            long i = minutes % 10;
            if (i == 1)
                sb.append(" minute ");
            else if (i < 5)
                sb.append(" minuty ");
            else
                sb.append(" minut ");
        }
        if (seconds > 0) {
            sb.append(seconds);
            long i = seconds % 10;
            if (i == 1)
                sb.append("sekunde ");
            else if (i < 5)
                sb.append("sekundy ");
            else
                sb.append("sekund ");
        }
        return (sb.toString());
    }

}
