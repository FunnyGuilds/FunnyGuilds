package net.dzikoysk.funnyguilds.rank.placeholders;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import dev.peri.yetanothermessageslibrary.replace.replacement.ComponentReplacement;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.config.message.EntityLocaleProvider;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.PlaceholdersService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import net.dzikoysk.funnyguilds.user.top.UserTop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class RankPlaceholdersService implements PlaceholdersService<User> {

    private static final Pattern TOP_PATTERN = Pattern.compile("\\{(PTOP|GTOP)-([A-Za-z_]+)-([0-9]+)}");
    private static final Pattern TOP_POSITION_PATTERN = Pattern.compile("\\{(POSITION|G-POSITION)-([A-Za-z_]+)}");

    private final EntityLocaleProvider entityLocaleProvider;

    private final PluginConfiguration config;
    private final MessageService messageService;
    private final UserRankManager userRankManager;
    private final GuildRankManager guildRankManager;

    public RankPlaceholdersService(
            EntityLocaleProvider entityLocaleProvider,
            PluginConfiguration config,
            MessageService messageService,
            UserRankManager userRankManager,
            GuildRankManager guildRankManager
    ) {
        this.entityLocaleProvider = entityLocaleProvider;
        this.config = config;
        this.messageService = messageService;
        this.userRankManager = userRankManager;
        this.guildRankManager = guildRankManager;
    }

    @Override
    public Replaceable asReplaceable(
            User targetUser,
            String prefix,
            String suffix,
            UnaryOperator<String> nameModifier
    ) {
        ComponentReplacement topReplacement = new ComponentReplacement(TOP_PATTERN) {
            @Override
            public @NotNull TextReplacementConfig getReplacement(@Nullable Locale locale) {
                return this.newReplacementBuilder()
                        .replacement((matchResult, builder) -> {
                            String topType = matchResult.group(1);
                            String comparatorType = matchResult.group(2);
                            String indexString = matchResult.group(3);
                            Option<Integer> indexOption = Option.supplyThrowing(
                                    NumberFormatException.class,
                                    () -> Integer.parseInt(indexString)
                            );
                            if (indexOption.isEmpty()) {
                                FunnyGuilds.getPluginLogger().error(indexString + "is invalid " + topType + " index!");
                                return builder;
                            }

                            int index = indexOption.get();
                            if (index < 1) {
                                FunnyGuilds.getPluginLogger().error("Index in " +
                                                                    topType +
                                                                    " must be greater or equal to 1!");
                                return builder;
                            }

                            switch (topType.toUpperCase(Locale.ROOT)) {
                                case "PTOP" -> {
                                    return RankPlaceholdersService.this.getPlayerTopComponent(
                                            comparatorType,
                                            index,
                                            targetUser
                                    );
                                }
                                case "GTOP" -> {
                                    return RankPlaceholdersService.this.getGuildTopComponent(
                                            comparatorType,
                                            index,
                                            targetUser
                                    );
                                }
                                default -> {
                                    return Component.empty();
                                }
                            }
                        })
                        .build();
            }
        };

        ComponentReplacement topPositionReplacement = new ComponentReplacement(TOP_POSITION_PATTERN) {
            @Override
            public @NotNull TextReplacementConfig getReplacement(@Nullable Locale locale) {
                return this.newReplacementBuilder()
                        .replacement((matchResult, builder) -> {
                            String positionType = matchResult.group(1);
                            String comparatorType = matchResult.group(2);

                            switch (positionType.toUpperCase(Locale.ROOT)) {
                                case "POSITION" -> {
                                    return getPlayerPositionComponent(
                                            comparatorType,
                                            targetUser
                                    );
                                }
                                case "G-POSITION" -> {
                                    return RankPlaceholdersService.this.getGuildPositionComponent(
                                            comparatorType,
                                            targetUser
                                    );
                                }
                                default -> {
                                    return Component.empty();
                                }
                            }
                        })
                        .build();
            }
        };
        
        return new FunnyFormatter()
                .register(topReplacement)
                .register(topPositionReplacement);
    }

    private Component getPlayerTopComponent(
            String comparatorType,
            int index,
            User targetUser
    ) {
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
        return this.formatUserTop(
                user,
                topValue,
                this.config.top.format.ptop,
                this.config.top.format.ptopValueFormatting.get(comparatorType.toLowerCase(Locale.ROOT))
        );
    }

    private Component getGuildTopComponent(
            String comparatorType,
            int index,
            User targetUser
    ) {
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
        return this.formatGuildTop(
                targetUser,
                guild,
                topValue,
                this.config.top.format.gtop,
                this.config.top.format.gtopValueFormatting.get(comparatorType.toLowerCase(Locale.ROOT))
        );
    }
    
    private Component formatUserTop(
            User user,
            Number topValue,
            String topFormat,
            @Nullable List<RangeFormatting> formats
    ) {
        boolean online = user.isOnline();
        if (online && this.config.ptopRespectVanish) {
            online = !user.isVanished();
        }
        TextColor applicableColor = online
                ? this.config.onlineColor
                : this.config.offlineColor;

        Component userNameComponent = Component.text(
                user.getName(),
                applicableColor
        );
        Component topValueComponent = formatTopValue(
                topValue,
                topFormat,
                formats
        );

        return userNameComponent.append(topValueComponent);
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
                guildTagComponent = this.config.relationalTag.chooseAndPrepareTag(
                        viewerGuild,
                        guild
                );
            }
        }

        if (guildTagComponent == null) {
            boolean hasOnlineMembers = guild.getMembers().stream()
                    .anyMatch(member -> {
                        boolean online = member.isOnline();
                        if (online && this.config.gtopRespectVanish) {
                            online = !member.isVanished();
                        }
                        return online;
                    });
            TextColor tagColor = hasOnlineMembers
                    ? this.config.onlineColor
                    : this.config.offlineColor;
            guildTagComponent = Component.text(
                    guild.getTag(),
                    tagColor
            );
        }

        Component topValueComponent = formatTopValue(
                topValue,
                topFormat,
                formats
        );
        return guildTagComponent.append(topValueComponent);
    }
    
    private static Component formatTopValue(
            Number topValue,
            String topFormat,
            @Nullable List<RangeFormatting> formats
    ) {
        String valueString = topValue instanceof Float || topValue instanceof Double
                ? String.format(
                Locale.US,
                "%.2f",
                topValue.floatValue()
        )
                : topValue.toString();
        String valueFormat = formats == null
                ? valueString
                : NumberRange.inRangeToString(
                        topValue,
                        formats
                );

        String rawTopFormat = topFormat
                .replace(
                        "{VALUE-FORMAT}",
                        valueFormat
                )
                .replace(
                        "{VALUE}",
                        valueString
                );

        return ComponentUtil.colored(rawTopFormat);
    }

    private static Component getPlayerPositionComponent(
            String comparatorType,
            User targetUser
    ) {
        if (targetUser == null) {
            return ComponentUtil.toComponent(0);
        }

        int position = targetUser.getRank().getPosition(comparatorType);
        return ComponentUtil.toComponent(position);
    }

    private Component getGuildPositionComponent(
            String comparatorType,
            User targetUser
    ) {
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

    @Override
    public Locale getEntityLocale(@Nullable Object entity) {
        return this.entityLocaleProvider.getEntityLocale(entity);
    }

}
