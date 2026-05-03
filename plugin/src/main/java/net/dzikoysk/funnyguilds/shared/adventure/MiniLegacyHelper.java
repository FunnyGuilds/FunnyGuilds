package net.dzikoysk.funnyguilds.shared.adventure;

import dev.peri.yetanothermessageslibrary.adventure.GlobalAdventureSerializer;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class MiniLegacyHelper {

    private static final Pattern SECTION_COLOR_PATTERN = Pattern.compile("(?i)§([0-9A-FK-OR])");
    private static final Pattern ALL_TEXT_PATTERN = Pattern.compile(".*");

    private static final Pattern HEX_TO_LEGACY_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})([0-9A-Fa-f]{1})");
    private static final String LEGACY_HEX_REPLACEMENT = "&x&$1&$2&$3&$4&$5&$6";

    private static final LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static final TextReplacementConfig COLOR_REPLACEMENTS = TextReplacementConfig.builder()
            .match(ALL_TEXT_PATTERN)
            .replacement((result, input) -> AMPERSAND_SERIALIZER.deserialize(result.group()))
            .build();
    static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .preProcessor(text -> {
                String processedText = HEX_TO_LEGACY_PATTERN.matcher(text).replaceAll(LEGACY_HEX_REPLACEMENT);  // convert simple hex format to legacy
                processedText = SECTION_COLOR_PATTERN.matcher(processedText).replaceAll("&$1"); // convert section to ampersand
                return processedText;
            })
            .postProcessor(component -> component.replaceText(COLOR_REPLACEMENTS))
            .build();

    private MiniLegacyHelper() {
    }
    
    static {
        GlobalAdventureSerializer.globalSerializer(MINI_MESSAGE);
    }
    
    public static MiniMessage miniMessage() {
        return MINI_MESSAGE;
    }

    public static Component colored(String text) {
        return MINI_MESSAGE.deserialize(text);
    }

}
