package net.dzikoysk.funnyguilds.rank.placeholders;

import dev.peri.yetanothermessageslibrary.replace.replacement.ComponentReplacement;
import java.util.Locale;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.dzikoysk.funnyguilds.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class TopPositionPlaceholderReplacement extends ComponentReplacement {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(POSITION|G-POSITION)-([A-Za-z_]+)}");

    private final MessageService messageService;
    private final GuildRankManager guildRankManager;
    private final User targetUser;

    TopPositionPlaceholderReplacement(MessageService messageService, GuildRankManager guildRankManager, User targetUser) {
        super(PLACEHOLDER_PATTERN);
        this.messageService = messageService;
        this.guildRankManager = guildRankManager;
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

        int position = targetUser.getRank().getPosition(comparatorType);
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

        Option<Guild> guildOption = targetUser.getGuild();
        if (guildOption.isEmpty()) {
            return minMembersToIncludeNoValue;
        }

        Guild guild = guildOption.get();
        if (!this.guildRankManager.isRankedGuild(guild)) {
            return minMembersToIncludeNoValue;
        }

        int position = guild.getRank().getPosition(comparatorType);
        return ComponentUtil.toComponent(position);
    }
}
