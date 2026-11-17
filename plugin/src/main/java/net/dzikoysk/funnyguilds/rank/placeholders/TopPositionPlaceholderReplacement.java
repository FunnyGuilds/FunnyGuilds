package net.dzikoysk.funnyguilds.rank.placeholders;

import dev.peri.yetanothermessageslibrary.replace.replacement.ComponentReplacement;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.rank.RankService;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.dzikoysk.funnyguilds.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TopPositionPlaceholderReplacement extends ComponentReplacement {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(POSITION|G-POSITION)-([A-Za-z0-9_:.-]+)}", Pattern.CASE_INSENSITIVE);

    private final MessageService messageService;
    private final RankService rankService;
    private final User targetUser;

    TopPositionPlaceholderReplacement(MessageService messageService, RankService rankService, User targetUser) {
        super(PLACEHOLDER_PATTERN);
        this.messageService = messageService;
        this.rankService = rankService;
        this.targetUser = targetUser;
    }

    @Override
    public @NotNull TextReplacementConfig getReplacement(@Nullable Locale locale) {
        return this.newReplacementBuilder()
            .replacement((matchResult, builder) -> {
                String positionType = matchResult.group(1);
                String comparatorType = matchResult.group(2);

                return switch (positionType.toUpperCase(Locale.ROOT)) {
                    case "POSITION" -> this.getPlayerPositionComponent(comparatorType, targetUser);
                    case "G-POSITION" -> this.getGuildPositionComponent(comparatorType, targetUser);
                    default -> Component.empty();
                };
            }).build();
    }

    private Component getPlayerPositionComponent(String comparatorType, User targetUser) {
        if (targetUser == null) {
            return ComponentUtil.toComponent(0);
        }

        int position = this.rankService.getUserRankTracker(comparatorType)
                .flatMap(tracker -> tracker.findPositionByEntity(targetUser)).orElse(0);
        return ComponentUtil.toComponent(position);
    }

    private Component getGuildPositionComponent(String comparatorType, User targetUser) {
        Component minMembersToIncludeNoValue = this.messageService.getComponent(
            targetUser,
            config -> config.minMembersToIncludeNoValue
        );

        if (targetUser == null) {
            return minMembersToIncludeNoValue;
        }

        Optional<Guild> guildOption = targetUser.getGuild();
        if (guildOption.isEmpty()) {
            return minMembersToIncludeNoValue;
        }

        return this.rankService.getGuildRankTracker(comparatorType)
                .flatMap(tracker -> tracker.findPositionByEntity(guildOption.orElseThrow()))
                .map(ComponentUtil::toComponent).orElse(minMembersToIncludeNoValue);
    }
}
