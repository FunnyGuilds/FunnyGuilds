package net.dzikoysk.funnyguilds.rank.placeholders;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.config.RawString;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.PlaceholdersService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import net.dzikoysk.funnyguilds.user.top.UserTop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import dev.peri.yetanothermessageslibrary.adventure.AdventureHelper;
import dev.peri.yetanothermessageslibrary.replace.Replaceable;

public class RankPlaceholdersService implements PlaceholdersService<User> {

    private static final Pattern TOP_PATTERN = Pattern.compile("\\{(PTOP|GTOP)-([A-Za-z_]+)-([0-9]+)}");
    private static final Pattern TOP_POSITION_PATTERN = Pattern.compile("\\{(POSITION|G-POSITION)-([A-Za-z_]+)}");
    private static final Pattern LEGACY_TOP_PATTERN = Pattern.compile("\\{(PTOP|GTOP)-([0-9]+)}");

    private final PluginConfiguration config;
    private final TablistConfiguration tablistConfig;
    private final MessageService messageService;
    private final UserRankManager userRankManager;
    private final GuildRankManager guildRankManager;

    public RankPlaceholdersService(
            PluginConfiguration config,
            TablistConfiguration tablistConfig,
            MessageService messageService,
            UserRankManager userRankManager,
            GuildRankManager guildRankManager
    ) {
        this.config = config;
        this.tablistConfig = tablistConfig;
        this.messageService = messageService;
        this.userRankManager = userRankManager;
        this.guildRankManager = guildRankManager;
    }

    /**
     * Format top and top position placeholders in text.
     *
     * @param text       text to format
     * @param targetUser user for which text will be formatted
     * @return formatted text
     */
    @Override
    public String format(String text, User targetUser) {
        text = this.formatTop(text, targetUser);
        text = this.formatTopPosition(text, targetUser);

        if (this.config.top.enableLegacyPlaceholders) {
            text = this.formatRank(text, targetUser);
        }

        return text;
    }

    /**
     * Format top placeholders (PTOP/GTOP-type-x) in text.
     *
     * @param text       text to format
     * @param targetUser user for which text will be formatted
     * @return formatted text
     */
    public String formatTop(String text, @Nullable User targetUser) {
        if (text == null) {
            return "";
        }

        if (!text.contains("TOP-")) {
            return text;
        }

        Matcher matcher = TOP_PATTERN.matcher(text);
        if (!matcher.find()) {
            return text;
        }

        String topType = matcher.group(1);
        String comparatorType = matcher.group(2);
        String indexString = matcher.group(3);

        Option<Integer> indexOption = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(indexString));
        if (indexOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().error(indexString + "is invalid " + topType + " index!");
            return text;
        }

        int index = indexOption.get();
        if (index < 1) {
            FunnyGuilds.getPluginLogger().error("Index in " + topType + " must be greater or equal to 1!");
            return text;
        }

        if (topType.equalsIgnoreCase("PTOP")) {
            String placeholder = "{PTOP-" + comparatorType + "-" + index + "}";
            String noValue = this.messageService.get(targetUser, config -> config.ptopNoValue);

            Option<UserTop> userTopOption = this.userRankManager.getTop(comparatorType);
            if (userTopOption.isEmpty()) {
                return FunnyFormatter.format(text, placeholder, noValue);
            }

            UserTop userTop = userTopOption.get();

            Option<User> userOption = userTop.getUser(index);
            if (userOption.isEmpty()) {
                return FunnyFormatter.format(text, placeholder, noValue);
            }

            User user = userOption.get();
            Number topValue = userTop.getComparator().getValue(user.getRank());
            String topFormat = this.config.top.format.ptop.getValue();

            if (!topFormat.isEmpty()) {
                List<RangeFormatting> formats = this.config.top.format.ptopValueFormatting.get(comparatorType.toLowerCase(Locale.ROOT));
                String valueFormat = formats == null ? topValue.toString() : NumberRange.inRangeToString(topValue, formats);

                FunnyFormatter formatter = new FunnyFormatter()
                        .register("{VALUE-FORMAT}", valueFormat)
                        .register("{VALUE}", topValue.toString());

                topFormat = formatter.format(topFormat);
            }

            return this.formatUserRank(text, placeholder, user, topFormat);
        }

        if (topType.equalsIgnoreCase("GTOP")) {
            String placeholder = "{GTOP-" + comparatorType + "-" + index + "}";
            String noValue = this.messageService.get(targetUser, config -> config.gtopNoValue);

            Option<GuildTop> guildTopOption = this.guildRankManager.getTop(comparatorType);
            if (guildTopOption.isEmpty()) {
                return FunnyFormatter.format(text, placeholder, noValue);
            }

            GuildTop guildTop = guildTopOption.get();

            Option<Guild> guildOption = guildTop.getGuild(index);
            if (guildOption.isEmpty()) {
                return FunnyFormatter.format(text, placeholder, noValue);
            }

            Guild guild = guildOption.get();
            Number topValue = guildTop.getComparator().getValue(guild.getRank());
            String topFormat = this.config.top.format.gtop.getValue();

            if (!topFormat.isEmpty()) {
                List<RangeFormatting> formats = this.config.top.format.gtopValueFormatting.get(comparatorType.toLowerCase(Locale.ROOT));
                String valueFormat = formats == null ? topValue.toString() : NumberRange.inRangeToString(topValue, formats);

                FunnyFormatter formatter = new FunnyFormatter()
                        .register("{VALUE-FORMAT}", valueFormat)
                        .register("{VALUE}", topValue.toString());

                topFormat = formatter.format(topFormat);
            }

            return this.formatGuildRank(text, placeholder, targetUser, guild, topFormat);
        }

        return text;
    }

    /**
     * Format top position placeholders (POSITION/G-POSITION-type) in text.
     *
     * @param text       text to format
     * @param targetUser user for which text will be formatted
     * @return formatted text
     */
    public String formatTopPosition(String text, @Nullable User targetUser) {
        if (text == null) {
            return "";
        }

        if (!text.contains("POSITION-")) {
            return text;
        }

        Matcher matcher = TOP_POSITION_PATTERN.matcher(text);
        if (!matcher.find()) {
            return text;
        }

        String positionType = matcher.group(1);
        String comparatorType = matcher.group(2);

        if (positionType.equalsIgnoreCase("POSITION")) {
            if (targetUser == null) {
                return FunnyFormatter.format(text, "{POSITION}", 0);
            }

            int position = targetUser.getRank().getPosition(comparatorType);
            return FunnyFormatter.format(text, "{POSITION-" + comparatorType + "}", position);
        }

        if (positionType.equalsIgnoreCase("G-POSITION")) {
            String minMembersToIncludeNoValue = this.messageService.get(targetUser, config -> config.minMembersToIncludeNoValue);
            if (targetUser == null) {
                return FunnyFormatter.format(text, "{G-POSITION}", minMembersToIncludeNoValue);
            }

            String placeholder = "{G-POSITION-" + comparatorType + "}";

            Option<Guild> guildOption = targetUser.getGuild();
            if (guildOption.isEmpty()) {
                return FunnyFormatter.format(text, placeholder, minMembersToIncludeNoValue);
            }

            Guild guild = guildOption.get();
            if (!this.guildRankManager.isRankedGuild(guild)) {
                return FunnyFormatter.format(text, placeholder, minMembersToIncludeNoValue);
            }

            return FunnyFormatter.format(text, placeholder, guild.getRank().getPosition(comparatorType));
        }

        return text;
    }

    // TODO Migrate all {PTOP/GTOP-x} placeholders to new {PTOP/GTOP-type-x} and remove this method

    /**
     * Format legacy top placeholders (PTOP/GTOP-x) in text
     *
     * @param text       text to format
     * @param targetUser user for which text will be formatted
     * @return formatted text
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "4.11.0")
    public String formatRank(String text, @Nullable User targetUser) {
        if (text == null) {
            return "";
        }

        if (!text.contains("TOP-")) {
            return text;
        }

        Matcher matcher = LEGACY_TOP_PATTERN.matcher(text);
        if (!matcher.find()) {
            return text;
        }

        String topType = matcher.group(1);
        String indexString = matcher.group(2);

        Option<Integer> indexOption = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(indexString));
        if (indexOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().error(indexString + "is invalid " + topType + " index!");
            return text;
        }

        int index = indexOption.get();
        if (index < 1) {
            FunnyGuilds.getPluginLogger().error("Index in " + topType + " must be greater or equal to 1!");
            return text;
        }

        if (topType.equalsIgnoreCase("PTOP")) {
            String placeholder = "{PTOP-" + index + "}";
            String noValue = this.messageService.get(targetUser, config -> config.ptopNoValue);

            Option<User> userOption = this.userRankManager.getUser(DefaultTops.USER_POINTS_TOP, index);
            if (userOption.isEmpty()) {
                return FunnyFormatter.format(text, placeholder, noValue);
            }

            User user = userOption.get();
            int points = user.getRank().getPoints();
            String pointsFormat = this.config.ptopPoints.getValue();

            if (!pointsFormat.isEmpty()) {
                FunnyFormatter formatter = new FunnyFormatter()
                        .register("{POINTS-FORMAT}", NumberRange.inRangeToString(points, this.config.pointsFormat))
                        .register("{POINTS}", points);

                pointsFormat = formatter.format(pointsFormat);
            }

            return this.formatUserRank(text, placeholder, user, pointsFormat);
        }

        if (topType.equalsIgnoreCase("GTOP")) {
            String placeholder = "{GTOP-" + index + "}";
            String noValue = this.messageService.get(targetUser, config -> config.gtopNoValue);

            Option<Guild> guildOption = this.guildRankManager.getGuild(DefaultTops.GUILD_AVG_POINTS_TOP, index);
            if (guildOption.isEmpty()) {
                return FunnyFormatter.format(text, placeholder, noValue);
            }

            Guild guild = guildOption.get();
            int points = guild.getRank().getAveragePoints();
            String pointsFormat = this.config.gtopPoints.getValue();

            if (!pointsFormat.isEmpty()) {
                FunnyFormatter formatter = new FunnyFormatter()
                        .register("{POINTS-FORMAT}", NumberRange.inRangeToString(points, this.config.pointsFormat))
                        .register("{POINTS}", points);

                pointsFormat = formatter.format(pointsFormat);
            }

            return this.formatGuildRank(text, placeholder, targetUser, guild, pointsFormat);
        }

        return text;
    }

    private String formatUserRank(String text, String placeholder, User user, String topFormat) {
        boolean online = user.isOnline();
        if (online && this.config.ptopRespectVanish) {
            online = !user.isVanished();
        }

        RawString onlineColor = online ? this.config.ptopOnline : this.config.ptopOffline;
        return FunnyFormatter.format(text, placeholder, onlineColor + user.getName() + topFormat);
    }

    private String formatGuildRank(String text, String placeholder, @Nullable User targetUser, Guild guild, String topFormat) {
        String prefix = "{TAG}";

        if (this.tablistConfig.useRelationshipColors) {
            Guild viewerGuild = targetUser != null ? targetUser.getGuild().orNull() : null;
            prefix = this.config.relationalTag.chooseAndPrepareTag(viewerGuild, guild);
        }

        String formattedPrefix = FunnyFormatter.format(prefix, "{TAG}", guild.getTag());
        return FunnyFormatter.format(text, placeholder, formattedPrefix + topFormat);
    }

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([A-Za-z0-9-_)]+)}");

    public Replaceable prepareReplacement(User targetUser) {
        return new Replaceable() {
            @Override
            public @NotNull String replace(@Nullable Locale locale, @NotNull String text) {
                return RankPlaceholdersService.this.format(text, targetUser);
            }

            @Override
            public @NotNull Component replace(@Nullable Locale locale, @NotNull Component text) {
                TextReplacementConfig topReplacement = TextReplacementConfig.builder()
                        .match(PLACEHOLDER_PATTERN)
                        .replacement(((result, input) -> {
                            String replacement = RankPlaceholdersService.this.format(result.group(), targetUser);
                            return AdventureHelper.legacyToComponent(replacement);
                        }))
                        .build();
                return text.replaceText(topReplacement);
            }
        };
    }

}
