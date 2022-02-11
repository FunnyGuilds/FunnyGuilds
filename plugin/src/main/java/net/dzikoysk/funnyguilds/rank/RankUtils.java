package net.dzikoysk.funnyguilds.rank;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.range.IntegerRange;
import net.dzikoysk.funnyguilds.config.range.NumberRange;
import net.dzikoysk.funnyguilds.config.range.formatting.NumberRangeFormatting;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.top.UserTop;
import org.apache.commons.lang3.StringUtils;
import panda.std.Option;

public class RankUtils {

    private static final Pattern TOP_PATTERN = Pattern.compile("\\{(PTOP|GTOP)-([A-Za-z_]+)-([0-9]+)}");
    private static final Pattern TOP_POSITION_PATTERN = Pattern.compile("\\{(POSITION|G-POSITION)-([A-Za-z_]+)}");
    private static final Pattern LEGACY_TOP_PATTERN = Pattern.compile("\\{(PTOP|GTOP)-([0-9]+)}");

    /**
     * Parse top placeholders (PTOP/GTOP-type-x) in text
     *
     * @param targetUser user for which text will be parsed
     * @param text       text to parse
     * @return parsed text
     */
    public static String parseTop(User targetUser, String text) {
        return parseTop(
                FunnyGuilds.getInstance().getPluginConfiguration(),
                FunnyGuilds.getInstance().getTablistConfiguration(),
                FunnyGuilds.getInstance().getMessageConfiguration(),
                FunnyGuilds.getInstance().getRankManager(),
                targetUser,
                text
        );
    }

    /**
     * Parse top placeholders (PTOP/GTOP-type-x) in text
     *
     * @param targetUser user for which text will be parsed
     * @param text       text to parse
     * @return parsed text
     */
    public static String parseTop(
            PluginConfiguration config,
            TablistConfiguration tablistConfig,
            MessageConfiguration messages,
            RankManager rankManager,
            User targetUser,
            String text
    ) {
        if (text == null) {
            return null;
        }

        if (!text.contains("TOP-")) {
            return text;
        }

        Matcher matcher = TOP_PATTERN.matcher(text);
        if (matcher.find()) {
            String topType = matcher.group(1);
            String comparatorType = matcher.group(2);
            String indexString = matcher.group(3);

            int index;
            try {
                index = Integer.parseInt(indexString);
            }
            catch (NumberFormatException ex) {
                FunnyGuilds.getPluginLogger().error(indexString + "is invalid " + topType + " index!");
                return text;
            }

            if (index < 1) {
                FunnyGuilds.getPluginLogger().error("Index in " + topType + " must be greater or equal to 1!");
                return text;
            }

            if (topType.equalsIgnoreCase("PTOP")) {
                Option<UserTop> userTopOption = rankManager.getUserTop(comparatorType);
                if (userTopOption.isEmpty()) {
                    return StringUtils.replace(text, "{PTOP-" + comparatorType + "-" + index + "}", messages.ptopNoValue);
                }
                UserTop userTop = userTopOption.get();

                Option<User> userOption = userTop.getUser(index);
                if (userOption.isEmpty()) {
                    return StringUtils.replace(text, "{PTOP-" + comparatorType + "-" + index + "}", messages.ptopNoValue);
                }
                User user = userOption.get();

                Number topValue = userTop.getComparator().getValue(user.getRank());
                String topFormat = config.top.format.ptop.getValue();
                if (!topFormat.isEmpty()) {
                    List<NumberRangeFormatting> valueFormatting = config.top.format.ptopValueFormatting.get(comparatorType.toLowerCase());
                    topFormat = topFormat.replace("{VALUE-FORMAT}", valueFormatting == null
                            ? topValue.toString()
                            : NumberRange.inRangeToString(topValue, valueFormatting));
                    topFormat = topFormat.replace("{VALUE}", topValue.toString());
                }

                return formatUserRank(config, text, "{PTOP-" + comparatorType + "-" + index + "}", targetUser, user, topFormat);
            }
            else if (topType.equalsIgnoreCase("GTOP")) {
                Option<GuildTop> guildTopOption = rankManager.getGuildTop(comparatorType);
                if (guildTopOption.isEmpty()) {
                    return StringUtils.replace(text, "{GTOP-" + comparatorType + "-" + index + "}", messages.gtopNoValue);
                }
                GuildTop guildTop = guildTopOption.get();

                Option<Guild> guildOption = guildTop.getGuild(index);
                if (guildOption.isEmpty()) {
                    return StringUtils.replace(text, "{GTOP-" + comparatorType + "-" + index + "}", messages.gtopNoValue);
                }
                Guild guild = guildOption.get();

                Number topValue = guildTop.getComparator().getValue(guild.getRank());
                String topFormat = config.top.format.gtop.getValue();
                List<NumberRangeFormatting> valueFormatting = config.top.format.gtopValueFormatting.get(comparatorType.toLowerCase());
                topFormat = topFormat.replace("{VALUE-FORMAT}", valueFormatting == null
                        ? topValue.toString()
                        : NumberRange.inRangeToString(topValue, valueFormatting));
                topFormat = topFormat.replace("{VALUE}", topValue.toString());

                return formatGuildRank(config, tablistConfig, text, "{GTOP-" + comparatorType + "-" + index + "}", targetUser, guild, topFormat);
            }
        }

        return text;
    }

    /**
     * Parse legacy top placeholders (PTOP/GTOP-x) in text
     *
     * @param targetUser user for which text will be parsed
     * @param text       text to parse
     * @return parsed text
     */
    @Deprecated
    public static String parseRank(User targetUser, String text) {
        return parseRank(
                FunnyGuilds.getInstance().getPluginConfiguration(),
                FunnyGuilds.getInstance().getTablistConfiguration(),
                FunnyGuilds.getInstance().getMessageConfiguration(),
                FunnyGuilds.getInstance().getRankManager(),
                targetUser,
                text
        );
    }

    /**
     * Parse legacy top placeholders (PTOP/GTOP-x) in text
     *
     * @param targetUser user for which text will be parsed
     * @param text       text to parse
     * @return parsed text
     */
    @Deprecated
    public static String parseRank(
            PluginConfiguration config,
            TablistConfiguration tablistConfig,
            MessageConfiguration messages,
            RankManager rankManager,
            User targetUser,
            String text
    ) {
        if (text == null) {
            return null;
        }

        if (!text.contains("TOP-")) {
            return text;
        }

        Matcher matcher = LEGACY_TOP_PATTERN.matcher(text);
        if (matcher.find()) {
            String topType = matcher.group(1);
            String indexString = matcher.group(2);

            int index;
            try {
                index = Integer.parseInt(indexString);
            }
            catch (NumberFormatException ex) {
                FunnyGuilds.getPluginLogger().error(indexString + "is invalid " + topType + " index!");
                return text;
            }

            if (index < 1) {
                FunnyGuilds.getPluginLogger().error("Index in " + topType + " must be greater or equal to 1!");
                return text;
            }

            if (topType.equalsIgnoreCase("PTOP")) {
                Option<User> userOption = rankManager.getUser(index);
                if (userOption.isEmpty()) {
                    return StringUtils.replace(text, "{PTOP-" + index + "}", messages.ptopNoValue);
                }
                User user = userOption.get();

                int points = user.getRank().getPoints();
                String pointsFormat = config.ptopPoints.getValue();
                if (!pointsFormat.isEmpty()) {
                    pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRangeToString(points, config.pointsFormat));
                    pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
                }

                return formatUserRank(config, text, "{PTOP-" + index + "}", targetUser, user, pointsFormat);
            }
            else if (topType.equalsIgnoreCase("GTOP")) {
                Option<Guild> guildOption = rankManager.getGuild(index);
                if (guildOption.isEmpty()) {
                    return StringUtils.replace(text, "{GTOP-" + index + "}", messages.gtopNoValue);
                }
                Guild guild = guildOption.get();

                int points = guild.getRank().getAveragePoints();
                String pointsFormat = config.gtopPoints.getValue();
                if (!pointsFormat.isEmpty()) {
                    pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRangeToString(points, config.pointsFormat));
                    pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
                }

                return formatGuildRank(config, tablistConfig, text, "{GTOP-" + index + "}", targetUser, guild, pointsFormat);
            }
        }

        return text;
    }

    public static String parseTopPosition(
            User targetUser,
            String text
    ) {
        return parseTopPosition(
                FunnyGuilds.getInstance().getPluginConfiguration(),
                FunnyGuilds.getInstance().getMessageConfiguration(),
                FunnyGuilds.getInstance().getRankManager(),
                targetUser,
                text
        );
    }

    public static String parseTopPosition(
            PluginConfiguration config,
            MessageConfiguration messages,
            RankManager rankManager,
            User targetUser,
            String text
    ) {
        if (text == null) {
            return null;
        }

        if (!text.contains("POSITION-")) {
            return text;
        }

        Matcher matcher = TOP_POSITION_PATTERN.matcher(text);
        if (matcher.find()) {
            String positionType = matcher.group(1);
            String comparatorType = matcher.group(2);

            if (positionType.equalsIgnoreCase("POSITION")) {
                return StringUtils.replace(text, "{POSITION-" + comparatorType + "}", Integer.toString(targetUser.getRank().getPosition(comparatorType)
                        .orElseGet(0)));
            }
            else if (positionType.equalsIgnoreCase("G-POSITION")) {
                Guild guild = targetUser.getGuild();
                if (guild == null) {
                    return StringUtils.replace(text, "{G-POSITION-" + comparatorType + "}", messages.minMembersToIncludeNoValue);
                }

                return StringUtils.replace(text, "{G-POSITION-" + comparatorType + "}", rankManager.isRankedGuild(guild)
                        ? Integer.toString(guild.getRank().getPosition(comparatorType).orElseGet(0))
                        : messages.minMembersToIncludeNoValue);
            }
        }

        return text;
    }

    private static String formatUserRank(PluginConfiguration config, String text, String placeholder, User targetUser, User user, String topFormat) {
        boolean online = user.isOnline();
        if (online && config.ptopRespectVanish) {
            online = !user.isVanished();
        }

        return StringUtils.replace(text, placeholder, (online ? config.ptopOnline : config.ptopOffline) + user.getName() + topFormat);
    }

    private static String formatGuildRank(PluginConfiguration config, TablistConfiguration tablistConfig, String text, String placeholder, User targetUser, Guild guild, String topFormat) {
        String guildTag = guild.getTag();
        if (tablistConfig.playerListUseRelationshipColors) {
            guildTag = StringUtils.replace(config.prefixOther.getValue(), "{TAG}", guild.getTag());
            if (targetUser != null && targetUser.hasGuild()) {
                Guild sourceGuild = targetUser.getGuild();

                if (sourceGuild.getAllies().contains(guild)) {
                    guildTag = StringUtils.replace(config.prefixAllies.getValue(), "{TAG}", guild.getTag());
                }
                else if (sourceGuild.getEnemies().contains(guild)) {
                    guildTag = StringUtils.replace(config.prefixEnemies.getValue(), "{TAG}", guild.getTag());
                }
                else if (sourceGuild.getUUID().equals(guild.getUUID())) {
                    guildTag = StringUtils.replace(config.prefixOur.getValue(), "{TAG}", guild.getTag());
                }
            }
        }

        return StringUtils.replace(text, placeholder, guildTag + topFormat);
    }

    private RankUtils() {
    }

}
