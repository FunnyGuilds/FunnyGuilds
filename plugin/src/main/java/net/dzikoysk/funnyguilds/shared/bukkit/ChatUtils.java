package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import panda.std.stream.PandaStream;
import panda.utilities.StringUtils;

public final class ChatUtils {

    private ChatUtils() {
    }

    public static final Pattern DECOLOR_PATTERN = Pattern.compile("(?:\u00a7)([0-9A-Fa-fK-Ok-oRXrx][^\u00a7]*)");

    public static String colored(String message) {
        return message != null ? ChatColor.translateAlternateColorCodes('&', message) : "";
    }

    public static List<String> colored(List<String> messages) {
        return PandaStream.of(messages).map(ChatUtils::colored).toList();
    }

    public static String decolor(String coloredString) {
        return DECOLOR_PATTERN.matcher(coloredString).replaceAll("&$1");
    }

    public static String getLastColorBefore(String text, String before) {
        if (!text.contains(before)) {
            return "";
        }

        return ChatColor.getLastColors(StringUtils.split(text, before)[0]);
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
