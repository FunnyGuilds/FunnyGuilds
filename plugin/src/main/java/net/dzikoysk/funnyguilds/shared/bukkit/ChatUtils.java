package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import panda.std.stream.PandaStream;
import panda.utilities.StringUtils;

public final class ChatUtils {

    private ChatUtils() {
    }

    public static final Pattern DECOLOR_PATTERN = Pattern.compile("(?:\u00a7)([0-9A-Fa-fK-Ok-oRXrx][^\u00a7]*)");
    public static final String DECOLOR_REPLACEMENT = "&$1";

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{6})");

    private static final Pattern LEGACY_TO_HEX_PATTERN = Pattern.compile("&[xX]&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})");
    private static final String HEX_COLOR_REPLACEMENT = "&#$1$2$3$4$5$6";

    // Pattern for extracting last color code from text (section symbol based)
    private static final Pattern LAST_COLOR_PATTERN = Pattern.compile("(?:\u00a7[0-9A-Fa-fK-Ok-oRrXx])+");

    // Pattern for valid color codes after the ampersand
    private static final Pattern AMPERSAND_COLOR_PATTERN = Pattern.compile("&([0-9A-Fa-fK-Ok-oRrXx])");

    /**
     * Translates color codes from ampersand (&) format to section symbol (§) format.
     * Supports hex colors in &#RRGGBB format.
     * This is a direct replacement for ChatColor.translateAlternateColorCodes().
     */
    public static String colored(String message) {
        if (message == null) {
            return "";
        }
        // Convert hex colors (&#RRGGBB) to legacy format (&x&r&r&g&g&b&b) with lowercase
        Matcher hexMatcher = HEX_COLOR_PATTERN.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (hexMatcher.find()) {
            String hex = hexMatcher.group(1).toLowerCase();
            String replacement = "&x&" + hex.charAt(0) + "&" + hex.charAt(1) + "&" + hex.charAt(2) 
                    + "&" + hex.charAt(3) + "&" + hex.charAt(4) + "&" + hex.charAt(5);
            hexMatcher.appendReplacement(sb, replacement);
        }
        hexMatcher.appendTail(sb);
        message = sb.toString();
        // Replace &X with §X for valid color codes (0-9, a-f, k-o, r, x)
        return AMPERSAND_COLOR_PATTERN.matcher(message).replaceAll("\u00a7$1");
    }

    /**
     * Translates color codes for a list of messages.
     */
    public static List<String> colored(List<String> messages) {
        return PandaStream.of(messages).map(ChatUtils::colored).toList();
    }

    /**
     * Removes color codes from a string, converting section symbols (§) back to ampersand (&) format.
     */
    public static String decolor(String coloredString) {
        if (coloredString == null) {
            return null;
        }
        String rawString = DECOLOR_PATTERN.matcher(coloredString).replaceAll(DECOLOR_REPLACEMENT);
        rawString = LEGACY_TO_HEX_PATTERN.matcher(rawString).replaceAll(HEX_COLOR_REPLACEMENT);
        return rawString;
    }

    /**
     * Gets the last color code(s) before a specific substring in text.
     * Implementation using regex pattern matching instead of deprecated ChatColor.getLastColors().
     */
    public static String getLastColorBefore(String text, String before) {
        if (!text.contains(before)) {
            return "";
        }

        String textBefore = StringUtils.split(text, before)[0];
        Matcher matcher = LAST_COLOR_PATTERN.matcher(textBefore);
        String lastColors = "";
        while (matcher.find()) {
            lastColors = matcher.group();
        }
        return lastColors;
    }

    /**
     * Deserializes a string with section symbol (§) color codes to a Component.
     */
    public static Component deserializeSection(String text) {
        return LegacyComponentSerializer.legacySection().deserialize(text);
    }

    /**
     * Deserializes a string with ampersand (&) color codes to a Component.
     */
    public static Component deserializeAmpersand(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    /**
     * Converts a string with ampersand (&) color codes to a Component.
     * Supports hex colors in &#RRGGBB format.
     */
    public static Component toComponent(String text) {
        if (text == null) {
            return Component.empty();
        }
        // Convert hex colors first with lowercase
        Matcher hexMatcher = HEX_COLOR_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (hexMatcher.find()) {
            String hex = hexMatcher.group(1).toLowerCase();
            String replacement = "&x&" + hex.charAt(0) + "&" + hex.charAt(1) + "&" + hex.charAt(2) 
                    + "&" + hex.charAt(3) + "&" + hex.charAt(4) + "&" + hex.charAt(5);
            hexMatcher.appendReplacement(sb, replacement);
        }
        hexMatcher.appendTail(sb);
        text = sb.toString();
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
