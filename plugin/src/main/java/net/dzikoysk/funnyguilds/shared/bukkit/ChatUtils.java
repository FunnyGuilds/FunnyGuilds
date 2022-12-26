package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.List;
import java.util.regex.Matcher;
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

    public static final Pattern DECOLOR_PATTERN = Pattern.compile("(?:\u00a7)([0-9A-Fa-fK-Ok-oRXrx][^\u00a7]*)");

    public static String colored(String message) {
        return message != null ? hexColored("&#", ChatColor.translateAlternateColorCodes('&', message)) : "";
    }

    public static String hexColored(String startTag, String message) {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        final char colorChar = ChatColor.COLOR_CHAR;

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
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
