package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.config.util.IntegerRange;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;

public class RankUtils {

    public static String parseRank(User targetUser, String rankTop) {
        return parseRank(
                FunnyGuilds.getInstance().getPluginConfiguration(),
                FunnyGuilds.getInstance().getTablistConfiguration(),
                FunnyGuilds.getInstance().getMessageConfiguration(),
                RankManager.getInstance(),
                targetUser,
                rankTop
        );
    }

    public static String parseRank(
            PluginConfiguration config,
            TablistConfiguration tablistConfig,
            MessageConfiguration messages,
            RankManager rankManager,
            User targetUser,
            String rankTop
    ) {
        if (!rankTop.contains("TOP-")) {
            return null;
        }

        int index = getIndex(rankTop);

        if (index <= 0) {
            FunnyGuilds.getPluginLogger().error("Index in TOP- must be greater or equal to 1!");
            return null;
        }

        if (rankTop.contains("GTOP")) {
            Guild guild = rankManager.getGuild(index);

            if (guild == null) {
                return StringUtils.replace(rankTop, "{GTOP-" + index + '}', messages.gtopNoValue);
            }

            int points = guild.getRank().getAveragePoints();
            String pointsFormat = config.gtopPoints;

            if (!pointsFormat.isEmpty()) {
                pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRangeToString(points, config.pointsFormat));
                pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
            }

            String guildTag = guild.getTag();

            if (tablistConfig.playerListUseRelationshipColors) {
                guildTag = StringUtils.replace(config.prefixOther, "{TAG}", guild.getTag());

                if (targetUser != null && targetUser.hasGuild()) {
                    Guild sourceGuild = targetUser.getGuild();

                    if (sourceGuild.getAllies().contains(guild)) {
                        guildTag = StringUtils.replace(config.prefixAllies, "{TAG}", guild.getTag());
                    } else if (sourceGuild.getEnemies().contains(guild)) {
                        guildTag = StringUtils.replace(config.prefixEnemies, "{TAG}", guild.getTag());
                    } else if (sourceGuild.getUUID().equals(guild.getUUID())) {
                        guildTag = StringUtils.replace(config.prefixOur, "{TAG}", guild.getTag());
                    }
                }
            }

            return StringUtils.replace(rankTop, "{GTOP-" + index + '}', guildTag + pointsFormat);

        } else if (rankTop.contains("PTOP")) {
            User user = rankManager.getUser(index);

            if (user == null) {
                return StringUtils.replace(rankTop, "{PTOP-" + index + '}', messages.ptopNoValue);
            }

            int points = user.getRank().getPoints();
            String pointsFormat = config.ptopPoints;

            if (!pointsFormat.isEmpty()) {
                pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRangeToString(points, config.pointsFormat));
                pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
            }

            return StringUtils.replace(rankTop, "{PTOP-" + index + '}', (user.isOnline() ? config.ptopOnline : config.ptopOffline) + user.getName() + pointsFormat);
        }

        return null;
    }

    public static int getIndex(String rank) {
        StringBuilder sb = new StringBuilder();
        boolean open = false;
        boolean start = false;
        int result = -1;

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

        try {
            result = Integer.parseInt(sb.toString());
        } catch (NumberFormatException e) {
            FunnyGuilds.getPluginLogger().parser(rank + " contains an invalid number: " + sb.toString());
        }

        return result;
    }

    private RankUtils() {
    }

}
