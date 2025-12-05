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

    private static final Pattern HEX_TO_LEGACY_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})");
    private static final String LEGACY_COLOR_REPLACEMENT = "&x&$1&$2&$3&$4&$5&$6";

    private static final Pattern LEGACY_TO_HEX_PATTERN = Pattern.compile("&[xX]&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})&([0-9A-Fa-f]{1})");
    private static final String HEX_COLOR_REPLACEMENT = "&#$1$2$3$4$5$6";

    // Pattern for extracting last color code from text (section symbol based)
    private static final Pattern LAST_COLOR_PATTERN = Pattern.compile("(?:\u00a7[0-9A-Fa-fK-Ok-oRrXx])+");

    /**
     * Translates color codes from ampersand (&) format to section symbol (§) format.
     * Supports hex colors in &#RRGGBB format.
     * Uses Adventure API's LegacyComponentSerializer for translation.
     */
    public static String colored(String message) {
        if (message == null) {
            return "";
        }
        // Convert hex colors (&#RRGGBB) to legacy format (&x&R&R&G&G&B&B)
        message = HEX_TO_LEGACY_PATTERN.matcher(message).replaceAll(LEGACY_COLOR_REPLACEMENT);
        // Use Adventure's serializer to handle color code translation
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        return LegacyComponentSerializer.legacySection().serialize(component);
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
        // Convert hex colors first
        text = HEX_TO_LEGACY_PATTERN.matcher(text).replaceAll(LEGACY_COLOR_REPLACEMENT);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
