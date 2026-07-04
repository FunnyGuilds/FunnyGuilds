package net.dzikoysk.funnyguilds.rank.placeholders;

import dev.peri.yetanothermessageslibrary.replace.replacement.ComponentReplacement;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import net.dzikoysk.funnyguilds.user.top.UserTop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class TopPlaceholderReplacement extends ComponentReplacement {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(
        "\\{(PTOP|GTOP)-([A-Za-z_]+)-([0-9]+)(?:-(NAME|TAG|UUID|VALUE))?}",
        Pattern.CASE_INSENSITIVE
    );

    private final PluginConfiguration config;
    private final MessageService messageService;
    private final UserRankManager userRankManager;
    private final GuildRankManager guildRankManager;
    private final User targetUser;

    TopPlaceholderReplacement(
        PluginConfiguration config,
        MessageService messageService,
        UserRankManager userRankManager,
        GuildRankManager guildRankManager,
        User targetUser
    ) {
        super(PLACEHOLDER_PATTERN);
        this.config = config;
        this.messageService = messageService;
        this.userRankManager = userRankManager;
        this.guildRankManager = guildRankManager;
        this.targetUser = targetUser;
    }

    @Override
    public @NotNull TextReplacementConfig getReplacement(@Nullable Locale locale) {
        return this.newReplacementBuilder()
            .replacement((matchResult, builder) -> {
                String topType = matchResult.group(1);
                String comparatorType = matchResult.group(2);
                String indexString = matchResult.group(3);
                String subType = matchResult.group(4) == null
                    ? null
                    : matchResult.group(4).toUpperCase(Locale.ROOT);

                Option<Integer> indexOption = Option.supplyThrowing(NumberFormatException.class, () -> Integer.parseInt(indexString));
                if (indexOption.isEmpty()) {
                    FunnyGuilds.getPluginLogger().error(indexString + "is invalid " + topType + " index!");
                    return builder;
                }

                int index = indexOption.get();
                if (index < 1) {
                    FunnyGuilds.getPluginLogger().error("Index in " + topType + " must be greater or equal to 1!");
                    return builder;
                }

                return switch (topType.toUpperCase(Locale.ROOT)) {
                    case "PTOP" ->  this.getPlayerTopComponent(comparatorType, index, subType, targetUser);
                    case "GTOP" -> this.getGuildTopComponent(comparatorType, index, subType, targetUser);
                    default -> Component.empty();
                };
            }).build();
    }

    private Component getPlayerTopComponent(String comparatorType, int index, String subType, User targetUser) {
        Component noValue = this.messageService.getComponent(
            targetUser,
            config -> config.ptopNoValue
        );

        Option<UserTop> userTopOption = this.userRankManager.getTop(comparatorType);
        if (userTopOption.isEmpty()) {
            return noValue;
        }

        UserTop userTop = userTopOption.get();
        Option<User> userOption = userTop.getUser(index);
        if (userOption.isEmpty()) {
            return noValue;
        }

        User user = userOption.get();
        Number topValue = userTop.getComparator().getValue(user.getRank());

        if (subType == null) {
            return this.formatUserTop(
                user,
                topValue,
                this.config.top.format.ptop,
                this.config.top.format.ptopValueFormatting.get(comparatorType.toLowerCase(Locale.ROOT))
            );
        }

        return switch (subType) {
            case "NAME" -> Component.text(user.getName());
            case "UUID" -> Component.text(user.getUUID().toString());
            case "VALUE" -> Component.text(rawValueString(topValue));
            default -> Component.empty();
        };
    }

    private Component getGuildTopComponent(String comparatorType, int index, String subType, User targetUser) {
        Component noValue = this.messageService.getComponent(
            targetUser,
            config -> config.gtopNoValue
        );

        Option<GuildTop> guildTopOption = this.guildRankManager.getTop(comparatorType);
        if (guildTopOption.isEmpty()) {
            return noValue;
        }

        GuildTop guildTop = guildTopOption.get();
        Option<Guild> guildOption = guildTop.getGuild(index);
        if (guildOption.isEmpty()) {
            return noValue;
        }

        Guild guild = guildOption.get();
        Number topValue = guildTop.getComparator().getValue(guild.getRank());

        if (subType == null) {
            return this.formatGuildTop(
                targetUser,
                guild,
                topValue,
                this.config.top.format.gtop,
                this.config.top.format.gtopValueFormatting.get(comparatorType.toLowerCase(Locale.ROOT))
            );
        }

        return switch (subType) {
            case "NAME" -> Component.text(guild.getName());
            case "TAG" -> Component.text(guild.getTag());
            case "VALUE" -> Component.text(rawValueString(topValue));
            default -> Component.empty();
        };
    }

    private Component formatUserTop(User user, Number topValue, String topFormat, @Nullable List<RangeFormatting> formats) {
        boolean online = user.isOnline();
        if (online && this.config.ptopRespectVanish) {
            online = !user.isVanished();
        }

        Component userNameComponent = Component.text(
            user.getName(),
            online ? this.config.onlineColor : this.config.offlineColor
        );
        return userNameComponent.append(formatTopValue(topValue, topFormat, formats));
    }

    private Component formatGuildTop(
        User targetUser,
        Guild guild,
        Number topValue,
        String topFormat,
        @Nullable List<RangeFormatting> formats
    ) {
        Component guildTagComponent = null;
        if (this.config.top.useRelationshipColors && targetUser != null) {
            Guild viewerGuild = targetUser.getGuild().orNull();
            if (viewerGuild != null) {
                guildTagComponent = this.config.relationalTag.chooseAndPrepareTag(viewerGuild, guild);
            }
        }

        if (guildTagComponent == null) {
            TextColor tagColor = !guild.getOnlineMembers(!this.config.gtopRespectVanish).isEmpty()
                ? this.config.onlineColor
                : this.config.offlineColor;
            guildTagComponent = Component.text(guild.getTag(), tagColor);
        }

        return guildTagComponent.append(formatTopValue(topValue, topFormat, formats));
    }

    private Component formatTopValue(Number topValue, String topFormat, @Nullable List<RangeFormatting> formats) {
        String valueString = rawValueString(topValue);
        String valueFormat = formats == null ? valueString : NumberRange.inRangeToString(topValue, formats);
        String rawTopFormat = topFormat
            .replace("{VALUE-FORMAT}", valueFormat)
            .replace("{VALUE}", valueString);

        return ComponentUtil.colored(rawTopFormat);
    }

    private String rawValueString(Number topValue) {
        return topValue instanceof Float || topValue instanceof Double
            ? String.format(Locale.US, "%.2f", topValue.floatValue())
            : topValue.toString();
    }
}
