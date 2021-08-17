package net.dzikoysk.funnyguilds.shared.bukkit;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class ChatUtils {

    private ChatUtils() {}

    public static String colored(String message) {
        return message != null ? ChatColor.translateAlternateColorCodes('&', message) : null;
    }
    
    public static List<String> colored(List<String> messages) {
        List<String> colored = new ArrayList<>();
        for (String message : messages) {
            colored.add(colored(message));
        }
        
        return colored;
    }

    public static String toString(Collection<String> list, boolean send) {
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s);
            builder.append(',');

            if (send) {
                builder.append(' ');
            }
        }

        String s = builder.toString();
        if (send) {
            if (s.length() > 2) {
                s = s.substring(0, s.length() - 2);
            } else if (s.length() > 1) {
                s = s.substring(0, s.length() - 1);
            }
        }

        return s;
    }

    public static List<String> fromString(String s) {
        List<String> list = new ArrayList<>();
        if (s == null || s.isEmpty()) {
            return list;
        }

        list = Arrays.asList(s.split(","));
        return list;
    }

    public static String appendDigit(int number) {
        return number > 9 ? "" + number : "0" + number;
    }

    public static String appendDigit(String number) {
        return number.length() > 1 ? "" + number : "0" + number;
    }

    public static String getPercent(double dividend, double divisor) {
        return getPercent(dividend / divisor);
    }
    
    public static String getPercent(double fraction) {
        return String.format(Locale.US, "%.1f", 100.0D * fraction);
    }

}
