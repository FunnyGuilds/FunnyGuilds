package net.dzikoysk.funnyguilds.feature.hooks;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import dev.peri.yetanothermessageslibrary.replace.replacement.ComponentReplacement;
import java.util.Locale;
import java.util.regex.Pattern;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HookUtils {

    private static final Pattern BRACKET_PLACEHOLDER_PATTERN = Pattern.compile("[{]([^{}]+)[}]");
    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();

    private HookUtils() {
    }

    public static Replaceable placeholdersReplaceable(
            Player observer,
            Player target
    ) {
        return new ComponentReplacement(BRACKET_PLACEHOLDER_PATTERN) {
            @Override
            public @NotNull TextReplacementConfig getReplacement(@Nullable Locale locale) {
                return this.newReplacementBuilder()
                        .replacement((result, input) -> HookManager.PLACEHOLDER_API
                                .flatMap(api -> {
                                    String placeholder = result.group(1);
                                    String toReplace = "%" + placeholder + "%";
                                    return api.replacePlaceholders(
                                            observer,
                                            target,
                                            toReplace
                                    );
                                })
                                .<ComponentLike>map(LEGACY_SECTION::deserialize)
                                .orElseGet(input))
                        .build();
            }
        };
    }

    public static Replaceable placeholdersReplaceable(Player player) {
        return new ComponentReplacement(BRACKET_PLACEHOLDER_PATTERN) {
            @Override
            public @NotNull TextReplacementConfig getReplacement(@Nullable Locale locale) {
                return this.newReplacementBuilder()
                        .replacement((result, input) -> HookManager.PLACEHOLDER_API
                                .flatMap(api -> {
                                    String placeholder = result.group(1);
                                    String toReplace = "%" + placeholder + "%";
                                    return api.replacePlaceholders(
                                            player,
                                            toReplace
                                    );
                                })
                                .<ComponentLike>map(LEGACY_SECTION::deserialize)
                                .orElseGet(input))
                        .build();
            }
        };
    }

}
