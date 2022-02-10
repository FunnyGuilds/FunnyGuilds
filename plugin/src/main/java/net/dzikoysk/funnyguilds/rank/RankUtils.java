package net.dzikoysk.funnyguilds.rank;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.top.UserTop;
import org.apache.commons.lang3.StringUtils;
import panda.std.Option;

public class RankUtils {

    private static final Pattern RANK_PATTERN = Pattern.compile("\\{(PTOP|GTOP)-([A-Za-z_]+)-([0-9]+)}");

    public static String parseComparableRank(User targetUser, String text) {
        return parseComparableRank(
                FunnyGuilds.getInstance().getPluginConfiguration(),
                FunnyGuilds.getInstance().getTablistConfiguration(),
                FunnyGuilds.getInstance().getMessageConfiguration(),
                FunnyGuilds.getInstance().getRankManager(),
                targetUser,
                text
        );
    }

    public static String parseComparableRank(
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

        Matcher matcher = RANK_PATTERN.matcher(text);
        while (matcher.find()) {
            String topType = matcher.group(1);
            String comparatorType = matcher.group(2);
            String indexString = matcher.group(3);

            int index;
            try {
                index = Integer.parseInt(indexString);
            }
            catch (NumberFormatException ex) {
                FunnyGuilds.getPluginLogger().error(indexString + "is invalid " + topType + " index!");
                break;
            }

            if (index < 1) {
                FunnyGuilds.getPluginLogger().error("Index in " + topType + " must be greater or equal to 1!");
                break;
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
                    List<RangeFormatting> valueFormatting = config.top.format.ptopValueFormatting.get(comparatorType.toLowerCase());
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
                List<RangeFormatting> valueFormatting = config.top.format.gtopValueFormatting.get(comparatorType.toLowerCase());
                topFormat = topFormat.replace("{VALUE-FORMAT}", valueFormatting == null
                        ? topValue.toString()
                        : NumberRange.inRangeToString(topValue, valueFormatting));
                topFormat = topFormat.replace("{VALUE}", topValue.toString());

                return formatGuildRank(config, tablistConfig, text, "{GTOP-" + comparatorType + "-" + index + "}", targetUser, guild, topFormat);
            }
        }

        return text;
    }

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
            return null;
        }

        int index;
        try {
            index = Integer.parseInt(getIndexString(text));
        }
        catch (NumberFormatException ex) {
            return null;
        }

        if (index <= 0) {
            FunnyGuilds.getPluginLogger().error("Index in TOP- must be greater or equal to 1!");
            return null;
        }

        if (text.contains("GTOP")) {
            Option<Guild> guildOption = rankManager.getGuild(index);
            if (guildOption.isEmpty()) {
                return StringUtils.replace(text, "{GTOP-" + index + '}', messages.gtopNoValue);
            }
            Guild guild = guildOption.get();

            int points = guild.getRank().getAveragePoints();
            String pointsFormat = config.gtopPoints.getValue();
            if (!pointsFormat.isEmpty()) {
                pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", NumberRange.inRangeToString(points, config.pointsFormat));
                pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
            }

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

            return formatGuildRank(config, tablistConfig, text, "{GTOP-" + index + '}', targetUser, guild, pointsFormat);

        }
        else if (text.contains("PTOP")) {
            Option<User> userOption = rankManager.getUser(index);
            if (userOption.isEmpty()) {
                return StringUtils.replace(text, "{PTOP-" + index + '}', messages.ptopNoValue);
            }
            User user = userOption.get();

            int points = user.getRank().getPoints();
            String pointsFormat = config.ptopPoints.getValue();
            if (!pointsFormat.isEmpty()) {
                pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", NumberRange.inRangeToString(points, config.pointsFormat));
                pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
            }

            return formatUserRank(config, text, "{PTOP-" + index + "}", targetUser, user, pointsFormat);
        }

        return null;
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

    public static String getIndexString(String rank) {
        StringBuilder sb = new StringBuilder();
        boolean open = false;
        boolean start = false;

        for (char c : rank.toCharArray()) {
            boolean end = false;

            switch (c) {
                case '{':
                    open = true;
                    break;
                case '-':
                    start = true;
                    break;
                case '}':
                    end = true;
                    break;
                default:
                    if (open && start) {
                        sb.append(c);
                    }
            }

            if (end) {
                break;
            }
        }

        return sb.toString();
    }

    public static int getIndex(String parse) {
        int result = -1;
        try {
            result = Integer.parseInt(getIndexString(parse));
        }
        catch (NumberFormatException e) {
            FunnyGuilds.getPluginLogger().parser(parse + " contains an invalid number: " + parse);
        }
        return result;
    }

    private RankUtils() {
    }

}
