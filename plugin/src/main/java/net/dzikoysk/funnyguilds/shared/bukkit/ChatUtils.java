package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import panda.utilities.StringUtils;

public final class ChatUtils {

    private ChatUtils() {}

    public static final Pattern DECOLOR_PATTERN = Pattern.compile("(?:\u00a7)([0-9A-Fa-fK-Ok-oRXrx][^\u00a7]*)");

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

    public static String decolor(String coloredString) {
        return DECOLOR_PATTERN.matcher(coloredString).replaceAll("&$1");
    }

    public static String getLastColorBefore(String text, String before) {
        if (!text.contains(before)) {
            return StringUtils.EMPTY;
        }
        return ChatColor.getLastColors(StringUtils.split(text, before)[0]);
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
            }
            else if (s.length() > 1) {
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

    public static void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        sender.sendMessage(message);
    }

    public static void broadcastMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        Bukkit.broadcastMessage(message);
    }

}
