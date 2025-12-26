package net.dzikoysk.funnyguilds.feature.hooks;

import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public final class HookUtils {

    private static final Pattern BRACKET_PLACEHOLDER_PATTERN = Pattern.compile("[{]([^{}]+)[}]");
    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();

    private HookUtils() {
    }

    public static Component replacePlaceholders(
            Player observer,
            Player target,
            Component message
    ) {
        return HookManager.PLACEHOLDER_API
                .map(api -> {
                    TextReplacementConfig replacement = TextReplacementConfig.builder()
                            .match(BRACKET_PLACEHOLDER_PATTERN)
                            .replacement((result, input) -> {
                                String placeholder = result.group(1);
                                String replaced = api.replacePlaceholders(
                                        observer,
                                        target,
                                        "%" + placeholder + "%"
                                );
                                return LEGACY_SECTION.deserialize(replaced);
                            })
                            .build();
                    return message.replaceText(replacement);
                })
                .orElseGet(message);
    }

    public static Component replacePlaceholders(
            Player player,
            Component message
    ) {
        return HookManager.PLACEHOLDER_API
                .map(api -> {
                    TextReplacementConfig replacement = TextReplacementConfig.builder()
                            .match(BRACKET_PLACEHOLDER_PATTERN)
                            .replacement((result, input) -> {
                                String placeholder = result.group(1);
                                String replaced = api.replacePlaceholders(
                                        player,
                                        "%" + placeholder + "%"
                                );
                                return LEGACY_SECTION.deserialize(replaced);
                            })
                            .build();
                    return message.replaceText(replacement);
                })
                .orElseGet(message);
    }

}
