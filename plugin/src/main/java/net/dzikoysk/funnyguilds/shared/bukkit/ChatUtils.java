package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import panda.std.stream.PandaStream;
import panda.utilities.StringUtils;

public final class ChatUtils {

    private ChatUtils() {
    }

    public static final Pattern DECOLOR_PATTERN = Pattern.compile("(?:\u00a7)([0-9A-Fa-fK-Ok-oRXrx][^\u00a7]*)");
    public static final String DECOLOR_REPLACEMENT = "&$1";

    private static final Pattern HEX_TO_LEGACY_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})");
    private static final String LEGACY_COLOR_REPLACEMENT = "&x&$1&$2&$3&$4&$5&$6";

    private static final Pattern LEGACY_TO_HEX_PATTERN = Pattern.compile("&[xX]&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})");
    private static final String HEX_COLOR_REPLACEMENT = "&#$1$2$3$4$5$6";

    public static String colored(String message) {
        if (message == null) {
            return "";
        }
        message = HEX_TO_LEGACY_PATTERN.matcher(message).replaceAll(LEGACY_COLOR_REPLACEMENT);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> colored(List<String> messages) {
        return PandaStream.of(messages).map(ChatUtils::colored).toList();
    }

    public static String decolor(String coloredString) {
        if (coloredString == null) {
            return null;
        }
        String rawString = DECOLOR_PATTERN.matcher(coloredString).replaceAll(DECOLOR_REPLACEMENT);
        rawString = LEGACY_TO_HEX_PATTERN.matcher(rawString).replaceAll(HEX_COLOR_REPLACEMENT);
        return rawString;
    }

    public static String getLastColorBefore(String text, String before) {
        if (!text.contains(before)) {
            return "";
        }

        return ChatColor.getLastColors(StringUtils.split(text, before)[0]);
    }

}
