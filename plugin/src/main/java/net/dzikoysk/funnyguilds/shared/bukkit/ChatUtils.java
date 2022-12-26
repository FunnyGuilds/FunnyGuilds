package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.List;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import panda.std.stream.PandaStream;
import panda.utilities.StringUtils;

public final class ChatUtils {

    private ChatUtils() {
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})");
    private static final Pattern REVERSE_HEX_PATTERN = Pattern.compile("&[xX]&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})");

    public static final Pattern DECOLOR_PATTERN = Pattern.compile("(?:\u00a7)([0-9A-Fa-fK-Ok-oRXrx][^\u00a7]*)");

    public static String colored(String message) {
        if (message == null) {
            return "";
        }
        message = HEX_PATTERN.matcher(message).replaceAll("&x&$1&$2&$3&$4&$5&$6");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> colored(List<String> messages) {
        return PandaStream.of(messages).map(ChatUtils::colored).toList();
    }

    public static String decolor(String coloredString) {
        if (coloredString == null) {
            return null;
        }
        String rawString = DECOLOR_PATTERN.matcher(coloredString).replaceAll("&$1");
        rawString = REVERSE_HEX_PATTERN.matcher(rawString).replaceAll("&#$1$2$3$4$5$6");
        return rawString;
    }

    public static String getLastColorBefore(String text, String before) {
        if (!text.contains(before)) {
            return "";
        }

        return ChatColor.getLastColors(StringUtils.split(text, before)[0]);
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (FunnyStringUtils.isEmpty(message)) {
            return;
        }

        sender.sendMessage(message);
    }

    public static void broadcastMessage(String message) {
        if (FunnyStringUtils.isEmpty(message)) {
            return;
        }

        Bukkit.broadcastMessage(message);
    }

}
