package net.dzikoysk.funnyguilds.basic.rank;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RankUtils {

    public static String parseRank(User source, String var) {
        if (! var.contains("TOP-")) {
            return null;
        }

        int i = getIndex(var);

        if (i <= 0) {
            FunnyGuilds.getInstance().getPluginLogger().error("Index in TOP- must be greater or equal to 1!");
            return null;
        }

        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (var.contains("GTOP")) {
            List<Guild> rankedGuilds = new ArrayList<>();

            for (int in = 1; in <= RankManager.getInstance().guilds(); in++) {
                Guild guild = RankManager.getInstance().getGuild(in);

                if (guild != null && guild.getMembers().size() >= config.minMembersToInclude) {
                    rankedGuilds.add(guild);
                }
            }

            if (rankedGuilds.isEmpty() || i - 1 >= rankedGuilds.size()) {
                return StringUtils.replace(var, "{GTOP-" + i + '}', FunnyGuilds.getInstance().getMessageConfiguration().gtopNoValue);
            }
            else {
                Guild guild = rankedGuilds.get(i - 1);
                int points = guild.getRank().getPoints();
                String pointsFormat = config.gtopPoints;

                if (!pointsFormat.isEmpty()) {
                    pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRange(points, config.pointsFormat, "POINTS"));
                    pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
                }

                String guildTag = guild.getTag();

                if (config.playerListUseRelationshipColors) {
                    guildTag = StringUtils.replace(config.prefixOther, "{TAG}", guild.getTag());

                    if (source != null && source.hasGuild()) {
                        Guild sourceGuild = source.getGuild();

                        if (sourceGuild.getAllies().contains(guild)) {
                            guildTag = StringUtils.replace(config.prefixAllies, "{TAG}", guild.getTag());
                        }
                        else if (sourceGuild.getUUID().equals(guild.getUUID())) {
                            guildTag = StringUtils.replace(config.prefixOur, "{TAG}", guild.getTag());
                        }
                    }
                }

                return StringUtils.replace(var, "{GTOP-" + i + '}', guildTag + pointsFormat);
            }
        }
        else if (var.contains("PTOP")) {
            User user = RankManager.getInstance().getUser(i);

            if (user != null) {
                int points = user.getRank().getPoints();
                String pointsFormat = config.ptopPoints;

                if (!pointsFormat.isEmpty()) {
                    pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRange(points, config.pointsFormat, "POINTS"));
                    pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
                }

                return StringUtils.replace(var, "{PTOP-" + i + '}', (user.isOnline() ? config.ptopOnline : config.ptopOffline) + user.getName() + pointsFormat);
            }
            else {
                return StringUtils.replace(var, "{PTOP-" + i + '}', FunnyGuilds.getInstance().getMessageConfiguration().ptopNoValue);
            }
        }

        return null;
    }

    public static int getIndex(String var) {
        StringBuilder sb = new StringBuilder();
        boolean open = false;
        boolean start = false;
        int result = -1;

        for (char c : var.toCharArray()) {
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
        } catch(NumberFormatException e) {
            FunnyGuilds.getInstance().getPluginLogger().parser(var + " contains an invalid number: " + sb.toString());
        }

        return result;
    }

    private RankUtils() { }

}
