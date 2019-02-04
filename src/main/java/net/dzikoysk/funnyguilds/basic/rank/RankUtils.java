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

    public static String parseRank(String var) {
        if (!var.contains("TOP-")) {
            return null;
        }

        int i = getIndex(var);

        if(i <= 0) {
            FunnyGuilds.getInstance().getPluginLogger().error("Index in TOP- must be greater or equal to 1!");
            return null;
        }

        PluginConfiguration c = FunnyGuilds.getInstance().getPluginConfiguration();
        List<Guild> rankedGuilds = new ArrayList<>();

        for (int in = 1; in <= RankManager.getInstance().guilds(); in++) {
            Guild guild = RankManager.getInstance().getGuild(in);

            if (guild != null && guild.getMembers().size() >= c.minMembersToInclude) {
                rankedGuilds.add(guild);
            }
        }

        if (var.contains("GTOP")) {
            if (rankedGuilds.isEmpty() || i - 1 >= rankedGuilds.size()) {
                return StringUtils.replace(var, "{GTOP-" + i + '}', FunnyGuilds.getInstance().getMessageConfiguration().gtopNoValue);
            }
            else {
                Guild guild = rankedGuilds.get(i - 1);
                int points = guild.getRank().getPoints();
                String pointsFormat = c.gtopPoints;

                if (!pointsFormat.isEmpty()) {
                    pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRange(points, c.pointsFormat, "POINTS"));
                    pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
                }

                return StringUtils.replace(var, "{GTOP-" + i + '}', guild.getTag() + pointsFormat);
            }
        }
        else if (var.contains("PTOP")) {
            User user = RankManager.getInstance().getUser(i);

            if (user != null) {
                int points = user.getRank().getPoints();
                String pointsFormat = c.ptopPoints;

                if (!pointsFormat.isEmpty()) {
                    pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRange(points, c.pointsFormat, "POINTS"));
                    pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
                }

                return StringUtils.replace(var, "{PTOP-" + i + '}', (user.isOnline() ? c.ptopOnline : c.ptopOffline) + user.getName() + pointsFormat);
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
